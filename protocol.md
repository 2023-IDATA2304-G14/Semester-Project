# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Greenhouse - A fully fledged greenhouse which can contain several GreenhouseNodes. It can be
  controlled by several Control-panel nodes. Acts as a server.
* GreenhouseNode - a section or a part of the greenhouse which contains sensors and actuators.
* Control-panel node - a device connected to a network which visualizes status GreenhouseNodes
  and sends control commands to them. Acts as a client.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol

The protocol uses TCP as the underlying transport protocol. This is because we don't want our data to be
lost or altered, and we don't care about the speed of the data transfer.

Our solution enables the user to set a custom port number for the server to listen on. If the user does not select a port number, the server
will default to port 1238. The reason for choosing this port is that is not reserved by any service, and
is not used by any common services. This means that the user can use this port without having to worry about
other services using the same port. If the user wants they can also choose automatic port selection, which
will select a random ephemeral port. 



## The architecture

Each greenhouse is its own server, and each control panel is its own client. The control panel can connect to
any greenhouse server, the greenhouse server supports having multiple clients connected at the same time.
A client can send commands to the server and the server will automatically update all connected clients.
If a client disconnects, the server will continue to run and wait for new clients to connect. One instance of
the greenhouse application can only run one server, but the user could run multiple instances of the application
if they have a use for multiple greenhouses.


## The flow of information and events

### Greenhouse server
The greenhouse server is the main node in the network. It listens for incoming connections from clients, running eac
in its own thread. When a client sends a command, the server will execute the command and send a response back to the
client. If the message is a BroadcastMessage, the server will send the message to all connected clients.

### Control panel
The control panel is the client in the network. It connects to the server and sends commands to it. The control panel
can also receive messages from the server, and will update the GUI accordingly. The control panel can also send
a subscription command to the server, which will make the server send updates to the control panel whenever a sensor
or actuator changes state.

## Connection and state

The communication protocol for this project is connection-oriented and stateful. This means that the nodes
in the network need to establish a connection before they can communicate with each other. The connection is 
persistent, meaning that the nodes can send multiple messages over the same connection. 


## Types, constants

Do you have some specific value types you use in several messages? They you can describe 
them here.

1. ActuatorDataMessage.java: 
Node ID: A numeric identifier for the node, commonly used to identify which sensor/actuator node the message pertains to.
Actuator ID: A numeric identifier for the specific actuator within the node.
Status Flag (boolean isOn): A boolean flag indicating the status (on/off) of the actuator.
Strength (int strength): An integer value representing the strength or level at which the actuator is operating.

2. SensorDataMessage.java:
Node ID (int nodeId): Similar to ActuatorDataMessage, identifying the node.
Sensor ID (int sensorId): A numeric identifier for the specific sensor within the node.
Value (double value): A floating-point number representing the sensor's reading.
Unit (String unit): A string indicating the unit of the sensor's reading.
Type (String type): A string specifying the type of sensor (e.g., temperature, humidity)

3. NodeStateMessage.java:
Node ID (int nodeId): Again, a numeric identifier for the node.
Name (String name): A string representing the name or label of the node.

## Message format

The general format of messages in this protocol is designed to ensure a consistent and structured
communication system. All the messages are encapsulated as Java records or classes, implementing
the "Message" interface which standardizes the behaviour across different message types. 

All message types implement the "Message" interface, ensuring a uniform approach to
serialization and deserialization. Each message type has a specific prefix that uniquely identifies it,
aiding in message processing and handling.

2. **Command interface:** Represents a set of actions or requests sent to the server. This implements an "execute"
    method that takes "GreenhouseSimulator" as a parameter. This method is responsible for executing the
    command and returning a "Message" object representing the commands outcome. While the interface itself doesnt 
    directly handle serialization, each implementation of "command" is expected to follow the protocols standard
    serialization/deserialization process. 

Here is a detailed format for each message type:
1. ActuatorDataMessage - The normally changing data of a Actuator (id, on/off, strength)
2. ActuatorRemovedMessage - A message signaling that a Actuator has been removed from the system
3. ActuatorStateMessage - A full state of a Actuator (id, on/off, strength, min, max, type, unit etc.) 
   used to update or add a Actuator to the UI
4. ErrorMessage - Compromises of a single string field to hold the error message. Converts
   the error message into a structured string format, embedding the prefix (e) for identification.
5. GetActuatorDataCommand - A command to get the data of an Actuator (nodeId, actuatorId)
6. GetActuatorsCommand - A command to get all the Actuators for a given nodeId (nodeId)
7. GetActuatorStateCommand - A command to get the state of an Actuator (nodeId, actuatorId)
8. GetNodesCommand - A command to get all the nodes in the system
9. GetSensorDataCommand - A command to get the data of a Sensor (nodeId, sensorId)
10. GetSensorsCommand - A command to get all the Sensors for a given nodeId (nodeId)
11. GetSensorStateCommand - A command to get the state of a Sensor (nodeId, sensorId)
12. NodeRemovedMessage - A message signaling that a Node has been removed from the system
13. NodeStateMessage - A full state of a Node (id, name) used to update or add a node to the UI
14. SensorDataMessage - The normally changing data of a Sensor (id, value)
15. SensorRemovedMessage - A message signaling that a Sensor has been removed from the system (nodeId, sensorId)
16. SensorStateMessage - A full state of a Sensor (id, value, min, max, type, unit etc.) used to update 
    or add a Sensor to the UI
17. SetActuatorCommand - A command to set the state of an Actuator (nodeId, actuatorId, on/off, strength)
18. SubscribeNodeCommand - A command to subscribe to a node (nodeId)
19. SubscribeNodeMessage - A message signaling that a node has been subscribed to (nodeId)
20. UnknownMessage - A message signaling that a message has been received that is not recognized by the system
21. UnsubscribeNodeCommand - A command to unsubscribe from a node (nodeId)
22. UnsubscribeNodeMessage - A message signaling that a node has been unsubscribed from (nodeId)

Each message type in this protocol is distinctly formatted to cater to its specific purpose. This design ensures
a robust and flexible communication framework within the greenhouse system.

### Error messages

In a system with nodes like a greenhouse control system, various components can encounter issues that need to be 
reported back to the user or system administrator. Here are som potential error messages that nodes might send, 
categorized by general types of errors:

#### Communication Errors:

- "Unable to establish connection with the server."
- "Connection timeout with the node."
- "Data transmission interrupted unexpectedly."
- "Unknown protocol received from the node."
- "Node is not responding to heartbeat signals."

#### Sensor Errors:

- "Sensor reading failed due to a timeout."
- "Invalid data received from the temperature/humidity/CO2 sensor."
- "Sensor disconnected or not detected."
- "Sensor calibration error detected."
- "Sensor ID not recognized."

#### Actuator Errors:

- "Actuator failed to execute the command." 
- "Actuator is not responding or offline."
- "Unexpected actuator position detected."
- "Actuator is blocked or jammed."
- "Overcurrent or overheating detected in actuator circuit."

#### System Health Errors:

- "Node battery level is critically low."
- "Memory overflow error on the node."
- "Node firmware is outdated and needs an update."
- "Hardware failure detected in node’s circuitry."
- "Node has rebooted unexpectedly, possible power issue."

#### Configuration and Setup Errors:

- "Configuration data is missing or corrupted."
- "Failed to apply new configuration settings."
- "Error during node initialization."
- "Node security credentials are invalid or expired."
- "Mismatch in node version compatibility."

#### Environmental Errors:

- "Ambient temperature is outside the safe operating range."
- "Humidity levels have exceeded recommended thresholds."
- "Detected water leak near the node."
- "Light intensity too high for optimal plant growth."
- "Air quality index has dropped below acceptable levels."

#### Security and Access Errors:

- "Unauthorized access attempt detected."
- "Node locked due to multiple failed login attempts."
- "Security certificate error."
- "Encrypted communication verification failed."
- "Suspicious activity detected; node is in lockdown mode."

These are some of the possible error messages that could occur by the nodes in the system.

## An example scenario

1. A node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. The first control panel gets a list of all nodes
6. The first control panel subscribes to the node with ID=1 and ID=2.
7. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
8. After a few seconds the two first Greenhousenodes send their sensordata to the control panel.
9. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
10. The other control-panel nodes gets the update from the server and updates its GUI.
11. The user of the second control-panel node presses on the button "turn off all actuators".
12. The other control-panel nodes gets the update from the server and updates its GUI.

## Reliability and security


### Security
Added a encryption system to more securely send messages over the Internet.
This uses an automatically generated PSK key that the users enter on the control panel
side. This is a 12 character long string that is displayed in the greenhouse GUI. 
The user need to enter this key into the control panel to be able to send and
receive data from the greenhouse.  
Security measurements:
* Encrypted with AES.
* PSK key is randomly generated.
* Need the key to be able to send/receive data.
* PSK key is only shown in the greenhouse GUI.
* Same concepts as a router, with the password on the back
* Improvements can be:
* * Use public and private key to send the symmetric key.
* * Let the user change the password.

in summary the solution supports security through symmetric encryption, specifically using AESin GCM
mode for secure and efficient message encryption and authentication. 