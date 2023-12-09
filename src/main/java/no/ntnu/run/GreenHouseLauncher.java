package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplicationMVC;
import no.ntnu.tools.Logger;

public class GreenHouseLauncher {
  public static void main(String[] args) {
    boolean fake = false;
    if (args.length == 1 && "fake".equals(args[0])) {
      fake = true;
      Logger.info("Using FAKE events");
    }
    GreenhouseApplicationMVC.startApp(fake);
  }

}
