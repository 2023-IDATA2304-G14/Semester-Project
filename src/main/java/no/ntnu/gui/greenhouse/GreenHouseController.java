package no.ntnu.gui.greenhouse;

import no.ntnu.encryption.ChangeKey;
import no.ntnu.encryption.PSKGenerator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.Sensor;

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

  // Method to generate a PSK (Pre-Shared Key) for encryption
  public String generatePSKKey(){
    String key = PSKGenerator.generateKey();
    ChangeKey.getInstance().setGreenhouseKeyKey(key);
    return key;
  }

  // Placeholder methods for future functionality
  public void addNode(GreenhouseNode node){
    model.getSimulator().addNode(node);
  }

  public void removeNode(GreenhouseNode node){
    model.getSimulator().removeNode(node);
  }

  public void addSensor(Sensor sensor) {
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
