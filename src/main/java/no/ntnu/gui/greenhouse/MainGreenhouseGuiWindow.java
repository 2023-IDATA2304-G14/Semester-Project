package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Accordion;


/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    private final Map<Integer, NodeGuiWindow> nodes = new HashMap<>(); // Change to NodeGuiWindow
    private BorderPane container;
    private VBox nodeDisplay;
    private ScrollPane scrollPane;
    private Accordion accordion;



    public MainGreenhouseGuiWindow() {
        super(new BorderPane(), WIDTH, HEIGHT);
        container = (BorderPane) this.getRoot();
        nodeDisplay = new VBox(10);
        scrollPane = new ScrollPane(nodeDisplay);
        scrollPane.setFitToWidth(true);
        accordion = new Accordion();
        scrollPane.setContent(accordion);
        mainWindow();
    }
    private void mainWindow() {
        nodeDisplay.getChildren().clear();
        for (Map.Entry<Integer, NodeGuiWindow> entry : nodes.entrySet()) {
            nodeDisplay.getChildren().add(entry.getValue());
        }
        container.setCenter(scrollPane); // Set the ScrollPane to the center
        container.setRight(createInfoLabel());
    }
    public void addNode(int nodeId, NodeGuiWindow nodeGui) {
        nodes.put(nodeId, nodeGui);
        accordion.getPanes().add(nodeGui.getTitledPane()); // Add the TitledPane to the Accordion
    }

    public void removeNode(int nodeId) {
        NodeGuiWindow nodeGui = nodes.remove(nodeId);
        if (nodeGui != null) {
            accordion.getPanes().remove(nodeGui.getTitledPane());
        }
    }

    private static Label createInfoLabel() {
        Label l = new Label("Close this window to stop the whole simulation");
        l.setWrapText(true);
        l.setPadding(new Insets(0, 0, 10, 0));
        return l;
    }
}

