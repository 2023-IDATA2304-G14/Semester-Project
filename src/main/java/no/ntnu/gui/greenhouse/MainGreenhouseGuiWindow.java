package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import no.ntnu.encryption.PSKGenerator;


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
        container.setTop(top());
        container.setCenter(scrollPane); // Set the ScrollPane to the center

        container.setRight(createInfoLabel());
    }

    private Node top(){
        HBox hBox = new HBox();
        Button button = new Button("Add Node");
        Button newPsk = new Button("Generate new PSK Key");
        Label label = new Label("PSK key");

        HBox left = new HBox(button);
        left.setSpacing(10);
        left.setPadding(new Insets(10,10,10,10));

        HBox right = new HBox(newPsk, label);
        right.setSpacing(10);
        right.setPadding(new Insets(10,10,10,10));
        right.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(left, right);

        newPsk.setOnAction(e -> label.setText(PSKGenerator.generateKey()));
        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(label.getText());
                clipboard.setContent(content);
            }
        });

        label.setText(PSKGenerator.generateKey());
        return hBox;
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

