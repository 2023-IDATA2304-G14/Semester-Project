package no.ntnu.greenhouse;

import no.ntnu.message.Message;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GreenhouseServer {
  public static final String DEFAULT_HOSTNAME = "localhost";
  public static final int DEFAULT_PORT = 1238;
  private final GreenhouseSimulator greenhouseSimulator;
  private int port;
  boolean isServerRunning;
  private final List<ClientHandler> connectedClients = new ArrayList<>();
  private ServerSocket serverSocket;
  private CountDownLatch portAssigned;

  /**
   * Create a greenhouse server that listens on the default port.
   * @param greenhouseSimulator The greenhouse simulator to use
   * @throws IllegalArgumentException If the greenhouse simulator is null
   */
  public GreenhouseServer(GreenhouseSimulator greenhouseSimulator) throws IllegalArgumentException {
    this(greenhouseSimulator, DEFAULT_PORT);
  }

  /**
   * Create a greenhouse server that listens on the specified port.
   * @param greenhouseSimulator The greenhouse simulator to use.
   * @param port The port to listen on. 0 means that the server will automatically pick a free port.
   * @throws IllegalArgumentException If the greenhouse simulator is null or the port is invalid.
   */
  public GreenhouseServer(GreenhouseSimulator greenhouseSimulator, int port) throws IllegalArgumentException {
    if (greenhouseSimulator == null) {
      throw new IllegalArgumentException("greenhouseSimulator cannot be null");
    }
    if (port < 0 || port > 65535) {
      throw new IllegalArgumentException("Invalid port number: " + port);
    }

    this.greenhouseSimulator = greenhouseSimulator;
    this.port = port;
  }

  /**
   * Start the server and wait for a port to be assigned.
   * @param portAssigned A countdown latch that will be counted down when a port has been assigned.
   * @throws IOException If the server could not be started.
   */
  public void startServer(CountDownLatch portAssigned) throws IOException {
    this.portAssigned = portAssigned;
    startServer();
  }

  /**
   * Start the server. This will block until the server is stopped.
   * @throws IOException If the server could not be started.
   */
  public void startServer() throws IOException {
    serverSocket = openListeningSocket();
    Logger.info("Server listening on port " + port);
    isServerRunning = true;
    while (isServerRunning) {
      ClientHandler clientHandler = acceptNextClientConnection(serverSocket);
      if (clientHandler != null) {
        connectedClients.add(clientHandler);
        clientHandler.start();
      }
    }
    portAssigned = null;
  }

  /**
   * Broadcast a message to all connected clients.
   * @param message The message to broadcast.
   */
  public void broadcastMessage(Message message) {
    Logger.info("Broadcasting message to " + connectedClients.size() + " clients");
    for (ClientHandler client : connectedClients) {
      client.sendMessageToClient(message);
    }
  }

  /**
   * Accept the next client connection. Creates a client handler for the client.
   * @param listeningSocket The socket to listen for connections on.
   * @return A client handler for the new client connection, or null if no connection could be accepted.
   */
  private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
    ClientHandler clientHandler = null;
    if (listeningSocket == null || listeningSocket.isClosed()) {
      return null;
    }
    try {
      Socket clientSocket = listeningSocket.accept();
      Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
      clientHandler = new ClientHandler(clientSocket, this);
    } catch (IOException e) {
      Logger.error("Could not accept client connection: " + e.getMessage());
    }
    return clientHandler;
  }

  /**
   * Open a socket that listens for connections. The port number will be set to the port number of the socket.
   * If the startServer method got provided a countdown latch, it will be counted down when the port has been assigned.
   * @return The socket that listens for connections.
   * @throws IOException If the socket could not be opened.
   */
  private ServerSocket openListeningSocket() throws IOException {
    ServerSocket listeningSocket = null;
    listeningSocket = new ServerSocket(port);
    port = listeningSocket.getLocalPort();
    if (portAssigned != null) {
      portAssigned.countDown();
    }
    return listeningSocket;
  }

  /**
   * Stops the server.
   * This will close all client connections and stop listening for new connections.
   */
  public void stopServer() {
    isServerRunning = false;

    if (!connectedClients.isEmpty()) {
      List<ClientHandler> clientsCopy = new ArrayList<>(connectedClients);

      ExecutorService executor = Executors.newFixedThreadPool(clientsCopy.size());

      try {
        for (ClientHandler clientHandler : clientsCopy) {
          executor.execute(() -> {
            try {
              clientHandler.close();
              connectedClients.remove(clientHandler);
            } catch (IOException e) {
              Logger.error("Could not close client connection: " + e.getMessage());
            }
          });
        }
      } finally {
        executor.shutdown();
      }
    }

    try {
      Logger.info("Closing server socket");
      serverSocket.close();
      serverSocket = null;
    } catch (IOException e) {
      Logger.error("Could not close server socket: " + e.getMessage());
    }
  }

  /**
   * Remove a client from the list of connected clients.
   * @param client The client to remove.
   */
  public void removeClient(ClientHandler client) {
    connectedClients.remove(client);
  }

  /**
   * Get the greenhouse simulator used by this server.
   * @return The greenhouse simulator.
   */
  public GreenhouseSimulator getGreenhouseSimulator() {
    return greenhouseSimulator;
  }

  /**
   * Get the port that the server is listening on.
   * @return The port number.
   */
  public int getPort() {
    return port;
  }

  /**
   * Get the countdown latch that will be counted down when a port has been assigned.
   * @return The countdown latch.
   */
  public CountDownLatch getPortAssigned() {
    return portAssigned;
  }
}
