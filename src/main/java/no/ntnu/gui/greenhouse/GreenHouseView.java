package no.ntnu.gui.greenhouse;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.gui.greenhouse.helper.NodeView;

public class GreenHouseView {
  private GreenHouseModel model;
  private GreenHouseController controller;
  private FlowPane flowPane = new FlowPane();

  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private GreenhouseNode node = null;

  public GreenHouseView(Stage stage){
    model = new GreenHouseModel();
    controller = new GreenHouseController(model, this);
    initialize(stage);
  }

  private Node top(){
    HBox hBox = new HBox();
    Button button = new Button("Add New Node");

    Button newPsk = new Button("Generate New PSK Key");
    Label label = new Label("PSK key");
    Button setPort = new Button("Set Port Number");

    hBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");

    HBox left = new HBox(button);
    left.setSpacing(10);
    left.setPadding(new Insets(10,10,10,10));

    HBox right = new HBox(newPsk, label, setPort);
    right.setSpacing(10);
    right.setPadding(new Insets(10,10,10,10));
    right.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(left, right);

    newPsk.setOnAction(e -> label.setText(controller.generatePSKKey()));

    setPort.setOnAction(e -> {
      showCustomDialog();
    });

    button.setOnAction(e -> {
      System.out.println("Add Node");
      GreenhouseNode node = DeviceFactory.createNode(
              0,0,0,0,0,  "TestNode");
      addNode(node);

    });

    label.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(label.getText());
        clipboard.setContent(content);
      }
    });

    return hBox;
  }

  private Node center(){

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(flowPane);
    scrollPane.setFitToWidth(true);

    flowPane.setHgap(10);
    flowPane.setVgap(10);
    flowPane.setPadding(new Insets(10, 10, 10, 10));

    return scrollPane;
  }

  private void initialize(Stage stage){
    stage.setTitle("GreenHouse");
    BorderPane root = new BorderPane();
    Button button = new Button("Hei");
    root.setCenter(center());
    root.setTop(top());
    Scene scene = new Scene(root, 1200, 600);
    stage.setScene(scene);
    stage.show();

  }


  public void addNode(GreenhouseNode node){
    NodeView nodeView = new NodeView(node);
    flowPane.getChildren().add(nodeView.getPane());
  }


  public void setSimulator(GreenhouseNode node){
    System.out.println(node.getId());
    addNode(node);
  }

  public GreenHouseModel getModel(){
    return model;
  }

  private void showCustomDialog() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("SEt Port Number");

    VBox dialogLayout = new VBox(10);

    Label label = new Label("Enter Port Number:");
    TextField textField = new TextField();
    Button saveButton = new Button("Update");

    saveButton.setOnAction(e -> {
      if(isValidPort(textField.getText())){
        String enteredText = textField.getText();
        model.setPort(textField.getText());
        dialog.setResult("");
        dialog.close();
      }
    });

    dialogLayout.getChildren().addAll(label, textField, saveButton);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  private boolean isValidPort(String portText) {
    try {
      int port = Integer.parseInt(portText);
      return port > 0 && port <= 65535;
    } catch (NumberFormatException e) {
      return false;
    }
  }

}
