package no.ntnu.gui.greenhouse;

// Import statements
import no.ntnu.encryption.PSKGenerator;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GreenHouseController implements PropertyChangeListener {
  // Model and View references for MVC pattern
  private GreenHouseModel model;
  private GreenHouseView view;

  // Constructor to initialize the controller with model and view
  public GreenHouseController(GreenHouseModel model, GreenHouseView view){
    this.model = model;
    this.view = view;
  }

  // Method to add nodes to the view
  public void addNodes(GreenhouseNode nodes){
    // Add the node to the view
    view.addNode(nodes);

    // Iterate through actuators of the node and print their types
    for (Actuator act : nodes.getActuators()) {
      System.out.println(act.getType());
    }
  }

  // Method to generate a PSK (Pre-Shared Key) for encryption
  public String generatePSKKey(){
    return PSKGenerator.generateKey();
  }

  // Placeholder methods for future functionality
  public void addNode(){
    // Implementation for adding a single node
  }

  public void removeNode(){
    // Implementation for removing a node
  }

  public void addSensor(){
    // Implementation for adding a sensor
  }

  public void removeSensor(){
    // Implementation for removing a sensor
  }

  public void addActuator(){
    // Implementation for adding an actuator
  }

  public void removeActuator(){
    // Implementation for removing an actuator
  }

  // Method to handle property change events
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // Print the new value of the changed property
    System.out.println(evt.getNewValue().toString());
  }
}
