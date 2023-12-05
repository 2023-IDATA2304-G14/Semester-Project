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

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 500;
  public static final int HEIGHT = 500;
  private final Map<Integer, Parent> nodes = new HashMap<>();

  public MainGreenhouseGuiWindow() {
    super(createMainContent(), WIDTH, HEIGHT);
  }

  private static Parent createMainContent() {
    BorderPane container = new BorderPane();
    container.setPadding(new Insets(20));
    container.setLeft(createMasterImage());
    container.setRight(createInfoLabel());
    return container;
  }
  private static Label createInfoLabel() {
    Label l = new Label("Close this window to stop the whole simulation");
    l.setWrapText(true);
    l.setPadding(new Insets(0, 0, 10, 0));
    return l;
  }

  public void addNode(int nodeId, Parent nodeGui) {
    nodes.put(nodeId, nodeGui);
    updateGui();
  }

  private void updateGui() {
    // Create a new VBox for organizing nodes
    VBox leftVBox = new VBox(10); // VBox with spacing of 10

    // Iterate over the HashMap and add each node's GUI component to the VBox
    for (Map.Entry<Integer, Parent> entry : nodes.entrySet()) {
      leftVBox.getChildren().add(entry.getValue());
    }

    // Create a new BorderPane for the layout
    BorderPane borderPane = new BorderPane();
    borderPane.setLeft(leftVBox); // Set the VBox in the left region of the BorderPane

    // Optionally, you can set other regions or leave them empty
    // borderPane.setTop(someTopNode);
    // borderPane.setCenter(someCenterNode);
    // borderPane.setRight(someRightNode);
    // borderPane.setBottom(someBottomNode);

    // Set the BorderPane or updated content to the appropriate part of your scene
    BorderPane container = (BorderPane) this.getRoot();
    container.setCenter(borderPane);
  }



  private static Node createMasterImage() {
    Node node;
    try {
      InputStream fileContent = new FileInputStream("images/Herman.png");
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(fileContent));
      imageView.setFitWidth(300);
      imageView.setPreserveRatio(true);
      node = imageView;
    } catch (FileNotFoundException e) {
      node = new Label("Could not find image file: " + e.getMessage());
    }
    return node;
  }

}
