package no.ntnu.greenhouse;

import no.ntnu.message.*;
import no.ntnu.subcribers.NodeSubscriber;
import no.ntnu.tools.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread implements NodeSubscriber {
  private final Socket clientSocket;
  private final GreenhouseServer greenhouseServer;
  private final BufferedReader socketReader;
  private final PrintWriter socketWriter;
  private final List<GreenhouseNode> subscribedNodes = new ArrayList<>();

  /**
   * Create a client handler.
   * @param clientSocket The socket to communicate with the client.
   * @param greenhouseServer The greenhouse server that created this client handler.
   * @throws IOException If the socket could not be opened.
   */
  public ClientHandler(Socket clientSocket, GreenhouseServer greenhouseServer) throws IOException {
    this.clientSocket = clientSocket;
    this.greenhouseServer = greenhouseServer;
    this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
  }

  /**
   * Handle communication with a client.
   */
  @Override
  public void run() {
    Message response;
    do {
      Command clientCommand = readClientRequest();
      if (clientCommand != null) {
        Logger.info("Received from client: " + clientCommand);
        response = clientCommand.execute(greenhouseServer.getGreenhouseSimulator());
        if (response != null) {
          if (!(clientCommand instanceof GetCommand) && response instanceof BroadcastMessage) {
            greenhouseServer.broadcastMessage(response);
          } else {
            sendMessageToClient(response);
          }
          Logger.info("Sent to client: " + response);
        }
      } else {
        response = null;
      }
    } while (response != null);
    Logger.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
    greenhouseServer.removeClient(this);
  }

  /**
   * Read a command from the client.
   * @return The command read from the client, or null if the client disconnected.
   */
  private Command readClientRequest() {
    Message clientCommand = null;
    try {
      String rawClientRequest = socketReader.readLine();
      if (rawClientRequest == null) {
        return null;
      }
      clientCommand = MessageSerializer.deserialize(rawClientRequest);
      if (!(clientCommand instanceof Command)) {
        Logger.error("Received invalid request from client: " + clientCommand);
        clientCommand = null;
      }
    } catch (IOException e) {
      Logger.error("Could not read from client socket: " + e.getMessage());
    }
    return (Command) clientCommand;
  }

  /**
   * Send a message to the client.
   * @param message The message to send.
   */
  public void sendMessageToClient(Message message) {
    String serializedMessage = MessageSerializer.serialize(message);
    socketWriter.println(serializedMessage);
  }

  /**
   * Closes the client handler.
   * This will close the socket and the socket reader and writer.
   */
  public void close() throws IOException {
    Logger.info("Closing client handler for " + clientSocket.getRemoteSocketAddress());
    try {
      socketReader.close();
      socketWriter.close();
      clientSocket.close();
    } catch (IOException e) {
      Logger.error("Could not close client socket: " + e.getMessage());
    }
  }

  /**
   * An event that is fired every time an actuator changes state.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {

  }

  /**
   * An event that is fired every time sensor values are updated.
   *
   * @param sensors A list of sensors having new values (readings)
   */
  @Override
  public void sensorsUpdated(SensorCollection sensors) {

  }

  @Override
  public List<GreenhouseNode> getSubscribedNodes() {
    return subscribedNodes;
  }
}
