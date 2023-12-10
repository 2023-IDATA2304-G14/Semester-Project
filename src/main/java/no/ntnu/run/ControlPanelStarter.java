package no.ntnu.run;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.controlpanel.ControlPanelClient;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.tools.Logger;

/**
 * Starter class for the control panel.
 * Note: we could launch the Application class directly, but then we would have issues with the
 * debugger (JavaFX modules not found)
 */
public class ControlPanelStarter {
  private CommunicationChannel channel;

  /**
   * Entrypoint for the application.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake",
   *             emulate fake events, when it is either something else or not present,
   *             use real socket communication.
   */
  public static void main(String[] args) {
    ControlPanelStarter starter = new ControlPanelStarter();
    starter.start();
  }

  private void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    initiateSocketCommunication(logic);
    ControlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication();
  }


  private void initiateSocketCommunication(ControlPanelLogic logic) {
    channel = new ControlPanelChannel(logic);
    logic.setCommunicationChannel(channel);
  }

  private void stopCommunication() {

  }
}
