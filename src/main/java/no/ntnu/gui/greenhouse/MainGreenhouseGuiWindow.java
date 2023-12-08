package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 500;
  public static final int HEIGHT = 500;
  private final Map<Integer, Parent> nodes = new HashMap<>();
  private final BorderPane container;
  private final VBox nodeDisplay;

  public MainGreenhouseGuiWindow() {
    super(new BorderPane(), WIDTH, HEIGHT);
    container = (BorderPane) this.getRoot();
    nodeDisplay = new VBox(10);
    mainWindow();
  }
  private void mainWindow() {
    // Clear and repopulate NodeDisplay
    nodeDisplay.getChildren().clear();
    for (Map.Entry<Integer, Parent> entry : nodes.entrySet()) {
      nodeDisplay.getChildren().add(entry.getValue());
    }
    container.setLeft(nodeDisplay);
    container.setRight(createInfoLabel());
  }



  public void addNode(int nodeId, GreenhouseNodeGui nodeGui) {
    nodes.put(nodeId, nodeGui);
    nodeDisplay.getChildren().add(nodeGui);
  }

  public void removeNode(int nodeId) {
    nodes.remove(nodeId);
    mainWindow();
  }
  private static Label createInfoLabel() {
    Label l = new Label("Close this window to stop the whole simulation");
    l.setWrapText(true);
    l.setPadding(new Insets(0, 0, 10, 0));
    return l;
  }
}















