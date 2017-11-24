package draw;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import javafx.geometry.HPos;
import javafx.geometry.Insets;

import javafx.scene.text.Font;

import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.SnapshotParameters;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.net.URI;
import java.net.URL;

import draw.tool.*;

public final class DrawApp extends Application {

  private final Stage drawStage = new Stage(); // primary stage
  private final BorderPane root = new BorderPane(); // layout for primary stage
  private final Scene drawScene = new Scene(root, 800.0, 500.0);

  private final MenuBar menuBar = new MenuBar();

  private Canvas canvas = new Canvas(600.0, 800.0); // canvas under edit
  private GraphicsContext gc; // graphics context for canvas under edit
  private final Pane editor = new Pane(canvas); // holds canvas and shapes

  private final ToolManager toolManager = new ToolManager(editor);

  private File saveDestination;
  private FileChooser.ExtensionFilter extension;

  /* start: build UI */
  public final void start(Stage drawStage) {

    // setup primary stage and scene
    drawStage.setTitle("Draw");
    drawStage.setMinHeight(100.0);
    drawStage.setMinWidth(150.0);

    root.setCenter(editor);

    editor.setBorder(new Border(new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            null, null,
            new Insets(10.0))));

    //build menubar
    root.setTop(menuBar);
    final Menu fileMenu = new Menu("File");
    final Menu editMenu = new Menu("Edit");

    final MenuItem newProjectItem = new MenuItem("New Project");
    newProjectItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) { newProject(); }
    });
    final MenuItem openItem = new MenuItem("Open Image");
    openItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        final FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"),
            new ExtensionFilter("All Files", "*.*"));
        File imgFile = fc.showOpenDialog(drawStage); //get file to open
        if (imgFile != null) {
          try {
            Image img = new Image(imgFile.toURI().toURL().toString());
            canvas = new Canvas(img.getWidth(), img.getHeight());
            gc = canvas.getGraphicsContext2D();
            gc.drawImage(img, 0.0, 0.0);
          } catch (Exception ex) {} //TODO: implement catch
        }
      }
    });
    final MenuItem exportItem = new MenuItem("Re-Export to Selected File");
    exportItem.setDisable(true);
    exportItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) { exportImage(); }
    });
    final MenuItem exportAsItem = new MenuItem("Export Image As...");
    exportAsItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        final FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
            new ExtensionFilter("png", "*.png"),
            new ExtensionFilter("jpg", "*.jpg"),
            new ExtensionFilter("bmp", "*.bmp"));
        saveDestination  = fc.showSaveDialog(drawStage); //get destination file
        extension = fc.getSelectedExtensionFilter(); //get desired extension
        exportItem.setDisable(false);
        exportImage(); // call export method
      }
    });

    fileMenu.getItems().addAll(newProjectItem, openItem, exportAsItem, 
      exportItem);
    menuBar.getMenus().addAll(fileMenu, editMenu); // add menus to menuBar
    root.setTop(menuBar); // add menuBar to scene

    // toolgrid
    final GridPane toolGrid = new GridPane(); // grid for draw tools
    toolGrid.setBorder(new Border(new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            null, null,
            new Insets(10.0))));
    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setPrefWidth(56);
    toolGrid.getColumnConstraints().addAll( //size all 4 columns to 25%
        columnConstraints, columnConstraints,
        columnConstraints, columnConstraints);
    toolGrid.setPadding(new Insets(15.0));

    final Label drawToolsLabel = new Label("Drawing Tools");
    drawToolsLabel.setFont(new Font(18.0)); // set size 18 font
    GridPane.setConstraints(drawToolsLabel, 0, 0, 4, 1); // set label position
    GridPane.setHalignment(drawToolsLabel, HPos.CENTER);
    toolGrid.getChildren().add(drawToolsLabel); // add to toolgrid

    final Button lineButton = new Button("Line");
    GridPane.setConstraints(lineButton, 0, 1); //set line button position
    toolGrid.getChildren().add(lineButton); // add to toolgrid
    lineButton.setOnAction(new EventHandler<ActionEvent>() { // set action
      @Override
      public void handle(ActionEvent e) {
        toolManager.setTool(toolManager.lineTool);
      }
    });

    final Button rectangleButton = new Button("Rectangle");
    GridPane.setConstraints(rectangleButton, 1, 1); // set rectangle btn posn
    toolGrid.getChildren().add(rectangleButton); // add to toolgrid
    rectangleButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        toolManager.setTool(toolManager.rectangleTool);
      }
    });

    final Button circleButton = new Button("Circle");
    GridPane.setConstraints(circleButton, 2, 1); // set circle btn position
    toolGrid.getChildren().add(circleButton); // add to toolgrid
    // TODO: create circle tool and set this event

    final Button pencilButton = new Button("Pencil");
    GridPane.setConstraints(pencilButton, 3, 1); // set pencil btn position
    toolGrid.getChildren().add(pencilButton); // add to toolgrid
    pencilButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        toolManager.setTool(toolManager.pencilTool);
      }
    });

    // colorpickers
    final ColorPicker primaryColorPicker = new ColorPicker(Color.BLACK);
    GridPane.setConstraints(primaryColorPicker, 0, 2, 2, 1); // set grid posn
    toolGrid.getChildren().add(primaryColorPicker);
    primaryColorPicker.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        toolManager.setActivePaint(primaryColorPicker.getValue());
      }
    });

    final ColorPicker secondaryColorPicker = new ColorPicker(Color.WHITE);
    GridPane.setConstraints(secondaryColorPicker, 2, 2, 2, 1); // set grid posn
    toolGrid.getChildren().add(secondaryColorPicker); // add to grid

    // button to quickly switch between primary and secondary colors
    final Button switchColorButton = new Button("Swap Colors");
    GridPane.setConstraints(switchColorButton, 0, 3, 4, 1); //set posn in grid
    GridPane.setHalignment(switchColorButton, HPos.CENTER);
    GridPane.setFillWidth(switchColorButton, true);
    toolGrid.getChildren().add(switchColorButton); //add to grid
    switchColorButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Color temp = secondaryColorPicker.getValue();
        secondaryColorPicker.setValue(primaryColorPicker.getValue());
        primaryColorPicker.setValue(temp);
        toolManager.setActivePaint(temp);
      }
    });

    // stroke width slider
    // label
    final Label widthLabel = new Label("Stroke Width:");
    GridPane.setConstraints(widthLabel, 0, 4, 4, 1);
    GridPane.setHalignment(widthLabel, HPos.CENTER);
    toolGrid.getChildren().add(widthLabel); //add to grid
    // slider control
    final Slider widthSlider = new Slider(
        1.0, 10.0, toolManager.getActiveWidth());
    GridPane.setConstraints(widthSlider, 1, 5, 3, 1);
    GridPane.setHalignment(widthSlider, HPos.CENTER);
    toolGrid.getChildren().add(widthSlider); //add to grid
    // readout of value
    final Label widthValue = new Label(
        Double.toString(widthSlider.getValue()));
    GridPane.setConstraints(widthValue, 0, 5);
    toolGrid.getChildren().add(widthValue); //add to grid

    // set slider action
    widthSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
        toolManager.setActiveWidth(new_val.doubleValue()); // set width
        widthValue.setText(String.format("%.2f", new_val));
      }
    });

    root.setLeft(toolGrid); // add toolgrid to borderpane

    drawStage.setScene(drawScene);
    drawStage.show(); // show window
  }

  private void exportImage() {
    if (saveDestination != null) {
      try {
        ImageIO.write(SwingFXUtils.fromFXImage(
              editor.snapshot(new SnapshotParameters(), null), null),
            extension.getDescription(), saveDestination);
      } catch (Exception ex) {}
    }
  }

  private void newProject() {
    canvas = new Canvas(600, 800);
    drawStage.sizeToScene();
  }

  public static void main(String [] args) {
    launch(args);
  }
}
