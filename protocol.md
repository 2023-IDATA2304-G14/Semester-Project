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

[//]: # (TODO: remove the following text)
TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you
choose this transport layer protocol?

The protocol uses TCP as the underlying transport protocol. This is because we don't want our data to be
lost or altered, and we don't care about the speed of the data transfer.

Our solution enables the user to set a custom port number for the server to listen on. If the user does not select a port number, the server
will default to port 1238. The reason for choosing this port is that is not reserved by any service, and
is not used by any common services. This means that the user can use this port without having to worry about
other services using the same port. If the user wants they can also choose automatic port selection, which
will select a random ephemeral port. 



## The architecture

[//]: # (TODO: remove the following text)
TODO - show the general architecture of your network. Which part is a server? Who are clients? 
Do you have one or several servers? Perhaps include a picture here. 

Each greenhouse is its own server, and each control panel is its own client. The control panel can connect to
any greenhouse server, the greenhouse server supports having multiple clients connected at the same time.
A client can send commands to the server and the server will automatically update all connected clients.
If a client disconnects, the server will continue to run and wait for new clients to connect. One instance of
the greenhouse application can only run one server, but the user could run multiple instances of the application
if they have a use for multiple greenhouses.


## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on 
incoming packets? Perhaps split into several subsections, where each subsection describes one 
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

### Greenhouse server
The greenhouse server is the main node in the network. It listens for incoming connections from clients.

## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or 
stateless? 

## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe 
them here.

## Message format

TODO - describe the general format of all messages. Then describe specific format for each 
message type in your protocol.

### Error messages

TODO - describe the possible error messages that nodes can send in your system.

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".

## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.

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
