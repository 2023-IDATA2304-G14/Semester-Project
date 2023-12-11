package no.ntnu.controlpanel;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import java.util.Base64;

import no.ntnu.encryption.ChangeKey;
import no.ntnu.encryption.SymmetricEncryption;
import no.ntnu.greenhouse.GreenhouseServer;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.message.ActuatorDataMessage;
import no.ntnu.message.ActuatorRemovedMessage;
import no.ntnu.message.ActuatorStateMessage;
import no.ntnu.message.Command;
import no.ntnu.message.ErrorMessage;
import no.ntnu.message.ListCommand;
import no.ntnu.message.Message;
import no.ntnu.message.MessageSerializer;
import no.ntnu.message.NodeRemovedMessage;
import no.ntnu.message.NodeStateMessage;
import no.ntnu.message.NodeSubscriptionCommand;
import no.ntnu.message.SensorDataMessage;
import no.ntnu.message.SensorRemovedMessage;
import no.ntnu.message.SensorStateMessage;
import no.ntnu.message.SubscribeNodeMessage;
import no.ntnu.message.UnknownMessage;
import no.ntnu.message.UnsubscribeNodeMessage;
import no.ntnu.tools.Logger;

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
  private final GreenhouseEventListener logic;
  private String decryptionKey;

  /**
   * Construct a ControlPanel client with default hostname and port.
   *
   * @param logic The logic of the ControlPanel client
   */
  //  TODO: Add the correct listener type
  public ControlPanelClient(GreenhouseEventListener logic) {
    this(GreenhouseServer.DEFAULT_HOSTNAME, GreenhouseServer.DEFAULT_PORT, logic);
  }

  public void setDecryptionKey(String decryptionKey) {
    this.decryptionKey = decryptionKey;
    System.out.println(decryptionKey);
  }

  /**
   * Construct a ControlPanel client with a custom hostname and port.
   *
   * @param host The IP tp the host.
   * @param port The port tp the host.
   * @param logic The logic of the ControlPanel client.
   * @throws RuntimeException Throws a RuntimeException if there is an error.
   */
  public ControlPanelClient(
      String host,
      int port,
      GreenhouseEventListener logic
  ) throws RuntimeException {
    this.host = host;
    this.port = port;
    this.logic = logic;
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
   * When a response is received, the logic is notified by the handleMessage method.
   * #see handleMessage
   */
  private void startListeningThread() {
    new Thread(() -> {
      Message message = null;
      do {
        try {
          if (socketReader != null) {
            String serializedMessage = socketReader.readLine();

//            TODO: implement decryption


            String encryptionKey = ChangeKey.getInstance().getKey();

            System.out.println("ser " + serializedMessage);

            String decryptedMessage = SymmetricEncryption.decryptMessage(Base64.getDecoder().decode(serializedMessage), encryptionKey);
            message = MessageSerializer.deserialize(decryptedMessage);
            handleMessage(message);
          } else {
            message = null;
          }
        } catch (IOException e) {
          Logger.error("Error reading from server: " + e.getMessage());
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
      } while (message != null);
    }).start();
  }

  /**
   * Handles a message received from the server.
   *
   * @param message the message received from the server.
   */
  private void handleMessage(Message message) {
    if (message instanceof ActuatorDataMessage actuatorDataMessage) {
      logic.onActuatorDataChanged(
          actuatorDataMessage.nodeId(),
          actuatorDataMessage.actuatorId(),
          actuatorDataMessage.isOn(),
          actuatorDataMessage.strength()
      );
    } else if (message instanceof ActuatorRemovedMessage actuatorRemovedMessage) {
      logic.onActuatorRemoved(
          actuatorRemovedMessage.nodeId(),
          actuatorRemovedMessage.actuatorId()
      );
    } else if (message instanceof ActuatorStateMessage actuatorStateMessage) {
      logic.onActuatorStateChanged(
          actuatorStateMessage.nodeId(),
          actuatorStateMessage.actuatorId(),
          actuatorStateMessage.type(),
          actuatorStateMessage.on(),
          actuatorStateMessage.strength(),
          actuatorStateMessage.minStrength(),
          actuatorStateMessage.maxStrength(),
          actuatorStateMessage.unit()
      );
    } else if (message instanceof NodeRemovedMessage nodeRemovedMessage) {
      logic.onNodeRemoved(
          nodeRemovedMessage.nodeId()
      );
    } else if (message instanceof NodeStateMessage nodeStateMessage) {
      logic.onNodeStateChanged(
          nodeStateMessage.nodeId(),
          nodeStateMessage.name()
      );
    } else if (message instanceof SensorDataMessage sensorDataMessage) {
      logic.onSensorDataChanged(
          sensorDataMessage.nodeId(),
          sensorDataMessage.sensorId(),
          sensorDataMessage.value()
      );
    } else if (message instanceof SensorRemovedMessage sensorStateMessage) {
      logic.onSensorRemoved(
          sensorStateMessage.nodeId(),
          sensorStateMessage.sensorId()
      );
    } else if (message instanceof SensorStateMessage sensorStateMessage) {
      logic.onSensorStateChanged(
          sensorStateMessage.nodeId(),
          sensorStateMessage.sensorId(),
          sensorStateMessage.type(),
          sensorStateMessage.value(),
          sensorStateMessage.min(),
          sensorStateMessage.max(),
          sensorStateMessage.unit()
      );
    } else if (message instanceof SubscribeNodeMessage subscribeNodeMessage) {
      logic.onSubscribeNode(
          subscribeNodeMessage.nodeId()
      );
    } else if (message instanceof UnsubscribeNodeMessage unsubscribeNodeMessage) {
      logic.onUnsubscribeNode(
          unsubscribeNodeMessage.nodeId()
      );
    } else if (message instanceof ErrorMessage errorMessage) {
      logic.onErrorReceived(
          errorMessage.message()
      );
    } else if (message instanceof UnknownMessage unknownMessage) {
      logic.onUnknownMessageReceived(
          unknownMessage.message()
      );
    } else {
      Logger.error("Unhandled message received from server: " + message.getClass().getSimpleName());
    }
  }

  /**
   * Sends a command to the server.
   *
   * @param command the command to send to the server.
   * @return true if the command was successfully sent, false otherwise.
   */
  public boolean sendCommand(Message command) {
    if (!(command instanceof Command)
        && !(command instanceof NodeSubscriptionCommand)
        && !(command instanceof ListCommand)) {
      throw new IllegalArgumentException("Message must be a command");
    }
    if (socketWriter != null && socketReader != null) {
      try {
        String serializedCommand = MessageSerializer.serialize(command);
        System.out.println("Serialized message: " + serializedCommand);
//        TODO: implement encryption
        String encryptionKey = ChangeKey.getInstance().getKey();
        byte[] encryptedMessage = SymmetricEncryption.encryptMessage(serializedCommand, encryptionKey);

        String byteConvertion = Base64.getEncoder().encodeToString(encryptedMessage);
        System.out.println("Send string: " + byteConvertion);

        socketWriter.println(byteConvertion);
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
