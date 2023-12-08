package no.ntnu.gui.greenhouse;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.GreenhouseNode;

public class GreenhouseNodeGui extends VBox {
    private final GreenhouseNode node;
    private final ComboBox<String> dropdownMenu;

    public GreenhouseNodeGui(GreenhouseNode node) {
        this.node = node;
        this.dropdownMenu = new ComboBox<>();
        // Populate dropdownMenu and other UI elements
        this.getChildren().add(dropdownMenu);
        // Add other UI elements as needed
    }

    public GreenhouseNode getNode() {
        return node;
    }
}
