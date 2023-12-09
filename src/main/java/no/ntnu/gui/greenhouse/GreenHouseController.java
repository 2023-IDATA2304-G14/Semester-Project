package no.ntnu.gui.greenhouse;

import no.ntnu.encryption.PSKGenerator;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GreenHouseController implements PropertyChangeListener {
  private GreenHouseModel model;
  private GreenHouseView view;
  public GreenHouseController(GreenHouseModel model, GreenHouseView view){
    this.model = model;
    this.view = view;
  }

  public void addNodes(GreenhouseNode nodes){

    view.addNode(nodes);

    for (Actuator act : nodes.getActuators()) {
      System.out.println(act.getType());
    }
    //node.getActuators();
  }

  public String generatePSKKey(){
    String key = PSKGenerator.generateKey();
    return key;
  }

  public void addNode(){

  }

  public void removeNode(){

  }

  public void addSensor(){

  }

  public void removeSensor(){

  }

  public void addActuator(){

  }

  public void removeActuator(){

  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    System.out.println(evt.getNewValue().toString());
  }
}
