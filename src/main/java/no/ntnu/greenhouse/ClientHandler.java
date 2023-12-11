package no.ntnu.greenhouse;

import no.ntnu.encryption.ChangeKey;
import no.ntnu.encryption.SymmetricEncryption;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.SensorListener;
import no.ntnu.listeners.common.StateListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.message.*;
import no.ntnu.tools.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Base64;
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
//    greenhouseServer.getGreenhouseSimulator().addNodeStateListener(this);
//    greenhouseServer.getGreenhouseSimulator().addNodeListener(this);
  }

  /**
   * Handle communication with a client.
   */
  @Override
  public void run() {
    List<Message> responses;
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
          Logger.info("Executing list command");
          responses.addAll(listCommand.execute(greenhouseServer.getGreenhouseSimulator()));
        } else {
          Logger.error("Received invalid request from client: " + clientCommand);
          Logger.error("Instance of: " + clientCommand.getClass().getName());
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
    } while (responses != null);
    Logger.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
    greenhouseServer.removeClient(this);
    greenhouseServer.getGreenhouseSimulator().removeNodeStateListener(this);
    greenhouseServer.getGreenhouseSimulator().removeNodeListener(this);
    greenhouseServer.getGreenhouseSimulator().removeStateListener(this);
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
      String key = ChangeKey.getInstance().getGreenhouseKeyKey();
      System.out.println("String: " + rawClientRequest);
      System.out.println("Byte: " + Base64.getDecoder().decode(rawClientRequest));

      String decryptedMessage = SymmetricEncryption.decryptMessage(Base64.getDecoder().decode(rawClientRequest), key);


      System.out.println("Decrypt: " + decryptedMessage);
      Logger.info(decryptedMessage);
      clientCommand = MessageSerializer.deserialize(decryptedMessage);
      if (!(clientCommand instanceof Command) && !(clientCommand instanceof NodeSubscriptionCommand) && !(clientCommand instanceof ListCommand)) {
        Logger.error("Received invalid request from client: " + clientCommand);
        clientCommand = null;
      }
    } catch (IOException e) {
      Logger.error("Could not read from client socket: " + e.getMessage());
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidParameterSpecException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    return clientCommand;
  }

  /**
   * Send a message to the client.

   * @param message The message to send.
   */
  public void sendMessageToClient(Message message) {
    try {
    String serializedMessage = MessageSerializer.serialize(message);
    Logger.info("Sending to client(" + this.clientSocket.getRemoteSocketAddress() + ") : " + serializedMessage);
//    TODO: implement encryption
    String key = ChangeKey.getInstance().getGreenhouseKeyKey();

      byte[] encryptedMessage = SymmetricEncryption.encryptMessage(serializedMessage, key);
      String byteConversion = Base64.getEncoder().encodeToString(encryptedMessage);

      socketWriter.println(byteConversion);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    } catch (ShortBufferException e) {
      throw new RuntimeException(e);
    }


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
   * @param actuator The actuator that has been removed
   */
  @Override
  public void actuatorRemoved(Actuator actuator) {
    sendMessageToClient(new ActuatorRemovedMessage(actuator.getNodeId(), actuator.getId()));
  }

  /**
   * An event that is fired every time a sensor is removed from a node.
   *
   * @param sensor The sensor that has been removed
   */
  @Override
  public void sensorRemoved(Sensor sensor) {
    sendMessageToClient(new SensorRemovedMessage(sensor.getNodeId(), sensor.getId()));
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
