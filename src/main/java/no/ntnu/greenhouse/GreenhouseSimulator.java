package no.ntnu.greenhouse;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.StateListener;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<Integer, GreenhouseNode> nodes = new HashMap<>();
  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
  private final boolean fake;
  private GreenhouseServer greenhouseServer;
  private ExecutorService serverExecutor;


  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
    if (!fake) {
      greenhouseServer = new GreenhouseServer(this);
    }
  }

  /**
   * Create a greenhouse simulator.
   *
   * @param port The port to listen on. 0 means that the server will automatically pick a free port.
   */
  public GreenhouseSimulator(int port) {
    this.fake = false;
    this.greenhouseServer = new GreenhouseServer(this, port);
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters, String name) {
    GreenhouseNode node = DeviceFactory.createNode(
            temperature, humidity, windows, fans, heaters, name);
    nodes.put(node.getId(), node);
  }
  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (GreenhouseNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  private void initiateRealCommunication() {
    CountDownLatch serverStarted = new CountDownLatch(1);

    if (serverExecutor != null) {
      serverExecutor.shutdownNow();
    }

    serverExecutor = Executors.newSingleThreadExecutor();
    serverExecutor.execute(() -> {
      try {
        greenhouseServer.startServer(serverStarted);
      } catch (Exception e) {
        Logger.error("Could not start the server: " + e.getMessage());
      }
    });

    try {
      serverStarted.await();
    } catch (InterruptedException e) {
      Logger.info("Waiting for executor start interrupted: " + e.getMessage());
      Thread.currentThread().interrupt();
    }

    if (greenhouseServer.getPortAssigned() != null) {
      try {
        greenhouseServer.getPortAssigned().await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

//      The set port can be access this way:
//      runLater(() -> this.port.set(greenhouseServer.getPort()));

    } else {
      Logger.error("Could not get the port assigned");
    }
  }

  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (GreenhouseNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      greenhouseServer.stopServer();
      if (serverExecutor != null) {
        serverExecutor.shutdown();
      }
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(StateListener listener) {
    for (GreenhouseNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }

  /**
   * Get a node by its ID.
   * @param nodeId The ID of the node
   * @return The node, or null if no node with this ID exists
   */
  public GreenhouseNode getNode(int nodeId) {
    return nodes.get(nodeId);
  }

  public List<GreenhouseNode> getNodes() {
    return new ArrayList<>(nodes.values());
  }

  public void addNode(GreenhouseNode node) {
    nodes.put(node.getId(), node);
    node.start();
  }

  public void removeNode(GreenhouseNode node) {
    nodes.remove(node.getId());
    node.stop();
  }
}
