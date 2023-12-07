package no.ntnu.greenhouse;

import no.ntnu.message.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
  private final Socket clientSocket;
  private final GreenhouseServer greenhouseServer;
  private final BufferedReader socketReader;
  private final PrintWriter socketWriter;

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
        System.out.println("Received from client: " + clientCommand);
        response = clientCommand.execute(greenhouseServer.getGreenhouseSimulator());
        if (response != null) {
          if (!(clientCommand instanceof GetCommand) && response instanceof BroadcastMessage) {
            greenhouseServer.broadcastMessage(response);
          } else {
            sendResponseToClient(response);
          }
          System.out.println("Sent to client: " + response);
        }
      } else {
        response = null;
      }
    } while (response != null);
    System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
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
        System.err.println("Received invalid request from client: " + clientCommand);
        clientCommand = null;
      }
    } catch (IOException e) {
      System.err.println("Could not read from client socket: " + e.getMessage());
    }
    return (Command) clientCommand;
  }

  /**
   * Send a response to the client.
   * @param response The response to send.
   */
  public void sendResponseToClient(Message response) {
    String serializedResponse = MessageSerializer.serialize(response);
    socketWriter.println(serializedResponse);
  }

  /**
   * Close the client handler.
   */
  public void close() {
    try {
      socketReader.close();
      socketWriter.close();
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Could not close client socket: " + e.getMessage());
    }
  }
}
