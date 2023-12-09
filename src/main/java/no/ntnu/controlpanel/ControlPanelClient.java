package no.ntnu.controlpanel;

import no.ntnu.greenhouse.GreenhouseServer;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.message.Command;
import no.ntnu.message.Message;
import no.ntnu.message.MessageSerializer;
import no.ntnu.tools.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handling connection to the server.
 *
 * @author Anders Lund
 * @version 02.11.2023
 */
public class ControlPanelClient {
  private Socket socket;
  private BufferedReader socketReader;
  private PrintWriter socketWriter;
  private final String host;
  private final int port;
  //  TODO: Add the correct listener type
  private final GreenhouseEventListener listener;

  /**
   * Construct a ControlPanel client with default hostname and port.
   *
   * @param listener The listener of the ControlPanel client
   */
//  TODO: Add the correct listener type
  public ControlPanelClient(GreenhouseEventListener listener) {
    this(GreenhouseServer.DEFAULT_HOSTNAME, GreenhouseServer.DEFAULT_PORT, listener);
  }

  /**
   *
   *
   * @param host The IP tp the host.
   * @param port The port tp the host.
   * @param listener The listener of the ControlPanel client.
   * @throws RuntimeException Throws a RuntimeException if there is an error.
   */
  public ControlPanelClient(String host, int port, GreenhouseEventListener listener) throws RuntimeException {
    this.host = host;
    this.port = port;
    this.listener = listener;
    if (!startClient(host, port)) {
      throw new RuntimeException("Could not connect to server");
    }
    startListeningThread();
  }

  /**
   * Starts the client and connects to the server.
   * Prints an error message if the connection fails.
   *
   * @return true if the client successfully connected to the server, false otherwise.
   */
  private boolean startClient(String host, int port) {
    try {
      socket = new Socket(host, port);
      socketWriter = new PrintWriter(socket.getOutputStream(), true);
      socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      return true;
    } catch (Exception e) {
      Logger.error("Error connecting to server: " + e.getMessage());
    }
    return false;
  }

  /**
   * Starts a listening thread that listens for responses from the server.
   * #see SOMELISTENER
   */
  private void startListeningThread() {
    new Thread(() -> {
      Message message = null;
      do {
        try {
          if (socketReader != null) {
            String serializedMessage = socketReader.readLine();
            message = MessageSerializer.deserialize(serializedMessage);
            handleMessage(message, listener);
          } else {
            message = null;
          }
        } catch (IOException e) {
          Logger.error("Error reading from server: " + e.getMessage());
        }
      } while (message != null);
    }).start();
  }

  /**
   * Handles a message received from the server.
   *
   * @param message the message received from the server.
   * @param listener the listener that will be notified when a response is received.
   */
//  TODO: Add handling of the different message types
  private void handleMessage(Message message, GreenhouseEventListener listener) {
//        if (message instanceof ChannelCountMessage channelCountMessage) {
//          listener.handleChannelCount(channelCountMessage.getChannelCount());
//        } else if (message instanceof TvStateMessage tvStateMessage) {
//          listener.handleTvState(tvStateMessage.isOn());
//        } else if (message instanceof CurrentChannelMessage currentChannelMessage) {
//          listener.handleCurrentChannel(currentChannelMessage.getChannel());
//        } else if (message instanceof ErrorMessage errorMessage) {
//          listener.handleErrorMessage(errorMessage.getMessage());
//        } else {
    Logger.error("Unhandled message received from server: " + message.getClass().getSimpleName());
//        }
  }

  /**
   * Sends a command to the server.
   *
   * @param command the command to send to the server.
   * @return true if the command was successfully sent, false otherwise.
   */
  public boolean sendCommand(Command command) {
    if (socketWriter != null && socketReader != null) {
      try {
        String serializedCommand = MessageSerializer.serialize(command);
        socketWriter.println(serializedCommand);
        return true;
      } catch (Exception e) {
        Logger.error("Error sending command to server: " + e.getMessage());
      }
    }
    return false;
  }

  /**
   * Stops the client by closing the socket and resetting the socket reader and writer.
   */
  public void stopClient() {
    try {
      if (socket != null) {
        socket.close();
      }
      socket = null;
      socketReader = null;
      socketWriter = null;
    } catch (IOException e) {
      Logger.error("Error closing socket: " + e.getMessage());
    }
  }

  public void reconnect() throws RuntimeException {
    stopClient();
    if (!startClient(host, port)) {
      throw new RuntimeException("Could not connect to server");
    }
    startListeningThread();
  }
}
