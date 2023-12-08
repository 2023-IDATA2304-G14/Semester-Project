package no.ntnu.gui.greenhouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    private final Map<Integer, NodeGuiWindow> nodes = new HashMap<>(); // Change to NodeGuiWindow
    private BorderPane container;
    private VBox nodeDisplay;

    public MainGreenhouseGuiWindow() {
        super(new BorderPane(), WIDTH, HEIGHT);
        container = (BorderPane) this.getRoot();
        nodeDisplay = new VBox(10);
        mainWindow();
    }
    private void mainWindow() {
        // Clear and repopulate NodeDisplay
        nodeDisplay.getChildren().clear();
        for (Map.Entry<Integer, NodeGuiWindow> entry : nodes.entrySet()) {
            nodeDisplay.getChildren().add(entry.getValue());
        }
        container.setLeft(nodeDisplay);
        container.setRight(createInfoLabel());
    }
    public void addNode(int nodeId, NodeGuiWindow nodeGui) {
        nodes.put(nodeId, nodeGui);
        nodeDisplay.getChildren().add(nodeGui); // Add the NodeGuiWindow to the display
    }

    public void removeNode(int nodeId) {
        NodeGuiWindow nodeGui = nodes.remove(nodeId);
        if (nodeGui != null) {
            nodeDisplay.getChildren().remove(nodeGui);
            nodes.remove(nodeId);
        }    mainWindow();
    }

    private static Label createInfoLabel() {
        Label l = new Label("Close this window to stop the whole simulation");
        l.setWrapText(true);
        l.setPadding(new Insets(0, 0, 10, 0));
        return l;
    }
}















