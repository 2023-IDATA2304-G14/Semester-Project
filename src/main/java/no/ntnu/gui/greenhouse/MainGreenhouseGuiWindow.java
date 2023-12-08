package no.ntnu.gui.greenhouse;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MainGreenhouseGuiWindow extends Scene {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    private final Map<Integer, NodeGuiWindow> nodes = new HashMap<>();
    private BorderPane container;
    private VBox nodeDisplay;

    public MainGreenhouseGuiWindow(GreenhouseSimulator simulator) {
        super(new BorderPane(), WIDTH, HEIGHT);
        container = (BorderPane) this.getRoot();
        nodeDisplay = new VBox(10);
        mainWindow();

        Button addNodeButton = new Button("Add Node");
        addNodeButton.setOnAction(event -> showAddNodeDialog(simulator));
        container.setRight(addNodeButton);
    }

    private void mainWindow() {
        nodeDisplay.getChildren().clear();
        for (Map.Entry<Integer, NodeGuiWindow> entry : nodes.entrySet()) {
            nodeDisplay.getChildren().add(entry.getValue());
        }
        container.setLeft(nodeDisplay);
        container.setRight(createInfoLabel());
    }

    public void addNode(int nodeId, NodeGuiWindow nodeGui) {
        nodes.put(nodeId, nodeGui);
        nodeDisplay.getChildren().add(nodeGui);
    }

    public void removeNode(int nodeId) {
        NodeGuiWindow nodeGui = nodes.remove(nodeId);
        if (nodeGui != null) {
            nodeDisplay.getChildren().remove(nodeGui);
        }
        mainWindow();
    }

    private static Label createInfoLabel() {
        Label l = new Label("Close this window to stop the whole simulation");
        l.setWrapText(true);
        l.setPadding(new Insets(0, 0, 10, 0));
        return l;
    }

    private void showAddNodeDialog(GreenhouseSimulator simulator) {
        TextInputDialog dialog = new TextInputDialog("1,1,1,1,1");
        dialog.setTitle("Add New Node");
        dialog.setHeaderText("Enter the number of sensors and actuators");
        dialog.setContentText("Format: tempSensors,humiditySensors,windows,fans,heaters");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nodeConfig -> {
            String[] parts = nodeConfig.split(",");
            if (parts.length == 5) {
                try {
                    int tempSensorCount = Integer.parseInt(parts[0]);
                    int humiditySensorCount = Integer.parseInt(parts[1]);
                    int windowCount = Integer.parseInt(parts[2]);
                    int fanCount = Integer.parseInt(parts[3]);
                    int heaterCount = Integer.parseInt(parts[4]);

                    GreenhouseNode newNode = simulator.addNewNode(tempSensorCount, humiditySensorCount, windowCount, fanCount, heaterCount);
                    NodeGuiWindow nodeGui = new NodeGuiWindow(newNode);
                    addNode(newNode.getId(), nodeGui);
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }
            }
        });
    }
}
