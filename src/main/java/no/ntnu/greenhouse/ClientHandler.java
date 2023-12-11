package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.SensorListener;
import no.ntnu.listeners.common.StateListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.message.ActuatorDataMessage;
import no.ntnu.message.ActuatorRemovedMessage;
import no.ntnu.message.ActuatorStateMessage;
import no.ntnu.message.BroadcastMessage;
import no.ntnu.message.Command;
import no.ntnu.message.GetCommand;
import no.ntnu.message.ListCommand;
import no.ntnu.message.Message;
import no.ntnu.message.MessageSerializer;
import no.ntnu.message.NodeRemovedMessage;
import no.ntnu.message.NodeStateMessage;
import no.ntnu.message.NodeSubscriptionCommand;
import no.ntnu.message.SensorDataMessage;
import no.ntnu.message.SensorRemovedMessage;
import no.ntnu.message.SensorStateMessage;
import no.ntnu.tools.Logger;

/**
 * A client handler handles communication with a client.
 * It is responsible for reading commands from the client,
 *  executing them, and sending the response back to the client.
 */
public class ClientHandler
    extends Thread
    implements
    ActuatorListener,
    SensorListener,
    NodeListener,
    NodeStateListener,
    StateListener {
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
    //    TODO: Improve the code quality of this method.
    List<Message> responses = null;
    do {
      Message clientCommand = readClientRequest();
      if (clientCommand != null) {
        responses = new ArrayList<>();
        Logger.info("Received from client: " + clientCommand);
        if (clientCommand instanceof NodeSubscriptionCommand nodeSubscriptionCommand) {
          responses.add(nodeSubscriptionCommand.execute(
              greenhouseServer.getGreenhouseSimulator(),
              this
          ));
        } else if (clientCommand instanceof Command command) {
          responses.add(command.execute(greenhouseServer.getGreenhouseSimulator()));
        } else if (clientCommand instanceof ListCommand listCommand) {
          responses.addAll(listCommand.execute(greenhouseServer.getGreenhouseSimulator()));
        } else {
          Logger.error("Received invalid request from client: " + clientCommand);
          responses = null;
        }
        if (responses != null && !responses.isEmpty()) {
          for (Message response : responses) {
            if ((!(clientCommand instanceof GetCommand)
                || !(clientCommand instanceof ListCommand))
                && response instanceof BroadcastMessage) {
              greenhouseServer.broadcastMessage(response);
            } else {
              sendMessageToClient(response);
            }
            Logger.info("Sent to client: " + response);
          }
        }
      } else {
        responses = null;
      }
    } while (responses != null && !responses.isEmpty());
    Logger.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
    greenhouseServer.removeClient(this);
  }

  /**
   * Read a command from the client.

   * @return The command read from the client, or null if the client disconnected.
   */
  private Message readClientRequest() {
    Message clientCommand = null;
    try {
      String rawClientRequest = socketReader.readLine();
      if (rawClientRequest == null) {
        return null;
      }
      //      TODO: implement decryption
      //      String decryptedMessage = SymmetricEncryption
      //        .decryptMessage(rawClientRequest, );
      clientCommand = MessageSerializer.deserialize(rawClientRequest);
      if (
          !(clientCommand instanceof Command)
          && !(clientCommand instanceof NodeSubscriptionCommand)
      ) {
        Logger.error("Received invalid request from client: " + clientCommand);
        clientCommand = null;
      }
    } catch (IOException e) {
      Logger.error("Could not read from client socket: " + e.getMessage());
    }
    return clientCommand;
  }

  /**
   * Send a message to the client.

   * @param message The message to send.
   */
  public void sendMessageToClient(Message message) {
    String serializedMessage = MessageSerializer.serialize(message);
    //    TODO: implement encryption
    //    Byte[] encryptedMessage = SymmetricEncryption
    //      .encryptMessage(serializedMessage, );
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
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorDataUpdated(Actuator actuator) {
    sendMessageToClient(
        new ActuatorDataMessage(
            actuator.getNodeId(),
            actuator.getId(),
            actuator.isOn(),
            actuator.getStrength()
        )
    );
  }

  /**
   * An event that is fired every time sensor values are updated.
   *
   * @param sensor A list of sensors having new values (readings)
   */
  @Override
  public void sensorDataUpdated(Sensor sensor) {
    SensorReading reading = sensor.getReading();
    sendMessageToClient(
        new SensorDataMessage(
            sensor.getNodeId(),
            sensor.getId(),
            reading.getValue(),
            reading.getUnit(),
            reading.getType()
        )
    );
  }


  /**
   * An event that is fired every time an actuator is removed from a node.
   *
   * @param nodeId     ID of the node on which this actuator is placed
   * @param actuatorId ID of the actuator that has been removed
   */
  @Override
  public void actuatorRemoved(int nodeId, int actuatorId) {
    sendMessageToClient(new ActuatorRemovedMessage(nodeId, actuatorId));
  }

  /**
   * An event that is fired every time a sensor is removed from a node.
   *
   * @param nodeId   ID of the node on which this sensor is placed
   * @param sensorId ID of the sensor that has been removed
   */
  @Override
  public void sensorRemoved(int nodeId, int sensorId) {
    sendMessageToClient(new SensorRemovedMessage(nodeId, sensorId));
  }

  /**
   * An event that is fired every time an actuator changes state.
   *
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorStateUpdated(Actuator actuator) {
    sendMessageToClient(
        new ActuatorStateMessage(
            actuator.getNodeId(),
            actuator.getId(),
            actuator.isOn(),
            actuator.getStrength(),
            actuator.getMinStrength(),
            actuator.getMaxStrength(),
            actuator.getUnit(),
            actuator.getType()
        )
    );
  }

  /**
   * An event that is fired every time a sensor changes state.
   *
   * @param sensor The sensor that has changed its state
   */
  @Override
  public void sensorStateUpdated(Sensor sensor) {
    SensorReading reading = sensor.getReading();
    sendMessageToClient(
        new SensorStateMessage(
            sensor.getNodeId(),
            sensor.getId(),
            reading.getType(),
            sensor.getMin(),
            sensor.getMax(),
            reading.getValue(),
            reading.getUnit()
        )
    );
  }

  /**
   * This event is fired when a sensor/actuator node has finished the starting procedure and
   * has entered the "ready" state.
   *
   * @param node the node which is ready now
   */
  @Override
  public void onNodeReady(GreenhouseNode node) {
    sendMessageToClient(new NodeStateMessage(node.getId(), node.getName()));
  }

  /**
   * This event is fired when a sensor/actuator node has stopped (shut down_.
   *
   * @param node The node which is stopped
   */
  @Override
  public void onNodeStopped(GreenhouseNode node) {
    sendMessageToClient(new NodeRemovedMessage(node.getId()));
  }
}
