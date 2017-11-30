package draw;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.*;

import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.SnapshotParameters;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.net.URI;
import java.net.URL;

import java.lang.Class;

import java.awt.Robot; /* for eyedropper */

import draw.tool.*;

public final class DrawApp extends Application {

  private final Stage drawStage = new Stage(); // primary stage
  private final BorderPane root = new BorderPane(); // layout for primary stage
  private final Scene drawScene = new Scene(root, 950.0, 650.0);

  private final MenuBar menuBar = new MenuBar();

  private Pane editorPane = new Pane(); // holds canvas and drawn shapes
  private final SubScene editorScene = new SubScene(editorPane, 600.0, 600.0);
  private Canvas canvas; // canvas under edit
  private GraphicsContext gc; // graphics context for canvas under edit

  private final ToolManager toolManager = new ToolManager(editorPane);

  private File imageFile;
  private FileChooser.ExtensionFilter fileExtension;

  final Stage popupStage = new Stage();
  Scene popupScene;

  /* start: build the UI, configure menus and toolbar */
  public final void start(Stage drawStage) {

    // init main stage
    drawStage.setTitle("Draw");
    drawStage.setMinWidth(150.0);
    drawStage.setMinHeight(200.0);
    drawStage.setScene(drawScene);

    popupStage.initModality(Modality.APPLICATION_MODAL); // popups take focus
    popupStage.initOwner(drawStage); // drawStage is owner of popup windows

    editorPane.setStyle("-fx-border-style: solid");
    root.setCenter(editorScene); // add editor to scene

    // check whether there are unsaved changes before closing
    drawStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent e) {
        e.consume(); // prevent stage from closing unless we tell it to
        if (!checkSaveState(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent x) { drawStage.close(); }
        }));
        else drawStage.close(); // no unsaved work
      }
    });

    buildMenuBar(); // populates menubar

    buildToolPane(); // populates toolgrid

    drawStage.show(); // show window
    }

    /* creates and assigns actions to menu entries */
    private void buildMenuBar() {

      root.setTop(menuBar); // add menuBar to stage

      final Menu fileMenu = new Menu("File");
      final Menu editMenu = new Menu("Edit");

      // File >> New Project
      final MenuItem newProjectItem = new MenuItem("New...");
      newProjectItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          // protect user from losing changes
          if(!checkSaveState(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent x) {
              newProject();
              popupStage.hide();
            }
          }));
          else newProject();
        }
      });
      // File >> Open Image
      final MenuItem openItem = new MenuItem("Open Image");
      openItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          final FileChooser fc = new FileChooser(); // new filechooser
          fc.getExtensionFilters().addAll(
              new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"),
              new ExtensionFilter("All Files", "*.*"));

          File imageFile = fc.showOpenDialog(drawStage); // get image to open
          loadImage(imageFile);
        }});
      // File >> Save
      final MenuItem exportItem = new MenuItem("Save");
      exportItem.setDisable(true); // disable until a file is available
      exportItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) { exportImage(); }
      });
      // File>>Save As
      final MenuItem exportAsItem = new MenuItem("Save As");
      exportAsItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          final FileChooser fc = new FileChooser();
          fc.getExtensionFilters().addAll(
              new ExtensionFilter("png", "*.png"),
              new ExtensionFilter("jpg", "*.jpg"),
              new ExtensionFilter("bmp", "*.bmp"));
          imageFile  = fc.showSaveDialog(drawStage); // choose dest. file
          fileExtension = fc.getSelectedExtensionFilter(); // choose type to save as
          exportItem.setDisable(false); // file has been chosen - enable 'save'
          exportImage();
        }
      });
      fileMenu.getItems().addAll(newProjectItem, openItem, exportAsItem,
          exportItem);

      // Edit >> Copy
      final MenuItem copyItem = new MenuItem("Copy");
      copyItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {}
      });

      // Edit >> Cut
      final MenuItem cutItem = new MenuItem("Cut");
      cutItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {}
      });

      // Edit >> Paste
      final MenuItem pasteItem = new MenuItem("Paste");
      pasteItem.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {}
      });

      editMenu.getItems().addAll(copyItem, cutItem, pasteItem);

      menuBar.getMenus().addAll(fileMenu, editMenu); // add menus to menuBar
    }

    /**
     * Builds and adds the tool pane containing drawing, text, color, and other
     * image editing tools to the left side of the stage.
     */
    private void buildToolPane() {

      final VBox toolBox = new VBox();
      root.setLeft(toolBox); // add to main stage

      // adjust toolBox characteristics
      toolBox.setSpacing(50.0); // spacing between sections in toolpane
      toolBox.setPadding(new Insets(10.0));


      // construct tilepane to hold drawing tools
      final Label drawToolsLabel = new Label("Drawing Tools");
      drawToolsLabel.setFont(new Font(14.0)); // size 14 font

      final TilePane tp = new TilePane();
      tp.setPrefColumns(4);
      tp.setPrefTileWidth(70.0);
      tp.setPrefTileHeight(15.0);

      final Button lineButton = new Button("Line");
      lineButton.setOnAction(new EventHandler<ActionEvent>() { // set action
        @Override
        public void handle(ActionEvent e) {
          toolManager.setTool(toolManager.lineTool);
        }
      });

      final Button rectangleButton = new Button("Rect'");
      rectangleButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          toolManager.setTool(toolManager.rectangleTool);
        }
      });

      final Button textButton = new Button("Text");
      textButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          toolManager.setTool(toolManager.textTool); // set text tool

          // text tool popup
          VBox vb = new VBox();
          vb.setSpacing(7.5);
          vb.setPadding(new Insets(10.0));

          popupScene = new Scene(vb, 220.0, 145.0);
          popupStage.setScene(popupScene);

          Label inputLabel = new Label("Enter desired text:");
          inputLabel.setFont(new Font(12));
          HBox inputLabelHBox = new HBox(inputLabel); // hbox for label
          inputLabelHBox.setAlignment(Pos.CENTER_LEFT);

          TextArea textArea = new TextArea();
          textArea.setPrefColumnCount(80); // adjust size of text input area
          textArea.setPrefRowCount(2);

          Button okButton = new Button("Place Text"); // submit button
          okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
              toolManager.textTool.setUserText(textArea.getText());
              popupStage.hide();
            }
          });
          okButton.setDefaultButton(true); // receives 'enter' press

          Button cancelButton = new Button("Cancel"); // cancel button
          cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
              popupStage.hide();
            }
          });
          cancelButton.setCancelButton(true); // receives 'esc' key press

          HBox buttonBox = new HBox(cancelButton, okButton);
          buttonBox.setPadding(new Insets(5.0));
          buttonBox.setSpacing(7.5);
          buttonBox.setAlignment(Pos.CENTER_RIGHT); // right align buttons

          vb.getChildren().addAll(inputLabelHBox, textArea, buttonBox);

          popupStage.show(); // show popup
        }
      });

      final Button pencilButton = new Button("Pencil");
      pencilButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          toolManager.setTool(toolManager.pencilTool);
        }
      });

      tp.getChildren().addAll(lineButton, rectangleButton, textButton,
          pencilButton);
      VBox toolAndLabelBox = new VBox(drawToolsLabel, tp);
      toolAndLabelBox.setSpacing(10.0);

      // colorpickers and dropper
      final GridPane gp = new GridPane();
      gp.setHgap(10.0);
      final ColorPicker primaryColorPicker = new ColorPicker(Color.BLACK);
      GridPane.setConstraints(primaryColorPicker, 0, 0); // set grid posn
      gp.getChildren().add(primaryColorPicker);
      primaryColorPicker.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          toolManager.setActivePaint(primaryColorPicker.getValue());
        }
      });

      final ColorPicker secondaryColorPicker = new ColorPicker(Color.WHITE);
      GridPane.setConstraints(secondaryColorPicker, 0, 2); // set grid posn
      gp.getChildren().add(secondaryColorPicker); // add to grid

      // button to quickly switch between primary and secondary colors
      final Button switchColorButton = new Button("<->");
      GridPane.setConstraints(switchColorButton, 0, 1); //set posn in grid
      GridPane.setHalignment(switchColorButton, HPos.CENTER);
      gp.getChildren().add(switchColorButton); //add to grid
      switchColorButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          Color temp = secondaryColorPicker.getValue();
          secondaryColorPicker.setValue(primaryColorPicker.getValue());
          primaryColorPicker.setValue(temp);
          toolManager.setActivePaint(temp);
        }
      });

      final Button dropperButton = new Button("Dropper");
      GridPane.setConstraints(dropperButton, 1, 1); // set posn in grid
      gp.getChildren().add(dropperButton); // add to grid
      dropperButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) { 
          editorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
              try {
                Robot robot = new Robot();
                java.awt.Color awtColor = /* awt color to javafx color */
                  robot.getPixelColor(
                      (int)e.getScreenX(), (int)e.getScreenY());
                int r = awtColor.getRed();
                int g = awtColor.getGreen();
                int b = awtColor.getBlue();
                int a = awtColor.getAlpha();
                double opacity = a / 255.0 ;
                Color fxColor = Color.rgb(r, g, b, opacity);
                primaryColorPicker.setValue(fxColor); /* set colorPicker */
              }
              catch(Exception f) {}
            }
          });
        }
      });

      // stroke width slider
      final GridPane gp2 = new GridPane();
      gp2.setHgap(10.0);
      final Label strokeLabel = new Label("Stroke Width:"); // label
      GridPane.setConstraints(strokeLabel, 0, 4, 4, 1);
      GridPane.setHalignment(strokeLabel, HPos.CENTER);
      gp2.getChildren().add(strokeLabel);
      final Slider widthSlider = new Slider(  // slider control
          1.0, 10.0, toolManager.getActiveWidth());
      widthSlider.setPrefWidth(180.0);
      GridPane.setConstraints(widthSlider, 1, 5);
      GridPane.setHalignment(widthSlider, HPos.CENTER);
      gp2.getChildren().add(widthSlider);
      final TextField strokeField = new TextField( // readout of value
          Double.toString(widthSlider.getValue())); // init to slider value
      strokeField.setPrefColumnCount(3);
      // set field action
      strokeField.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          try {
            toolManager.setActiveWidth(
                Double.parseDouble(strokeField.getText()));
          } catch (NumberFormatException except) {
            showError("The entered stroke width is invalid");
          }
        }
      });
      GridPane.setConstraints(strokeField, 0, 5);
      gp2.getChildren().add(strokeField);

      // set slider action
      widthSlider.valueProperty().addListener(new ChangeListener<Number>() {
        public void changed(ObservableValue<? extends Number> ov,
            Number old_val, Number new_val) {
          toolManager.setActiveWidth(new_val.doubleValue()); // set width
          strokeField.setText(String.format("%.2f", new_val));
        }
      });

      toolBox.getChildren().addAll(toolAndLabelBox, gp, gp2);
    }

    private void exportImage() {
      if (imageFile != null) {
        toolManager.setSaveState(true);
        try {
          ImageIO.write(SwingFXUtils.fromFXImage(
                editorPane.snapshot(new SnapshotParameters(), null), null),
              fileExtension.getDescription(), imageFile);
        } catch (Exception except) {}
      }
    }

    /**
     * Shows popup to get user-defined size and color for new image. Once
     * it has obtained image dimensions from the user, it creates a new canvas
     * of the user specified dimensions.
     * <p>
     * NewProject also clears the existing editor if there are nodes attached
     * to it.
     */
    private void newProject() {

      // construct popup
      VBox vb = new VBox();
      vb.setSpacing(7.5);
      vb.setPadding(new Insets(10.0));

      popupScene = new Scene(vb, 220.0, 180.0);
      popupStage.setScene(popupScene);

      GridPane gp1 = new GridPane(); // for template label and choicebox
      gp1.setVgap(5.0); gp1.setHgap(5.0);
      GridPane gp2 = new GridPane(); // for width/height input and labels
      gp2.setVgap(5.0); gp2.setHgap(5.0);

      Label sizeLabel = new Label("Image Size"); // font size label
      sizeLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

      Label widthLabel = new Label("Width:"); // width label
      widthLabel.setFont(new Font("System", 11));
      GridPane.setConstraints(widthLabel, 0, 0, 1, 1, HPos.LEFT, VPos.BOTTOM);
      TextField widthField = new TextField("640"); // width field
      widthField.setAlignment(Pos.BASELINE_LEFT);
      widthField.setFont(new Font("System", 11));
      widthField.setPrefColumnCount(8);
      GridPane.setConstraints(widthField, 1, 0, 1, 1, HPos.LEFT, VPos.BOTTOM);

      Label heightLabel = new Label("Height:"); // height label
      heightLabel.setFont(new Font("System", 11));
      GridPane.setConstraints(heightLabel, 0, 1, 1, 1, HPos.LEFT, VPos.BOTTOM);
      TextField heightField = new TextField("400"); // height field
      heightField.setAlignment(Pos.CENTER_LEFT);
      heightField.setFont(new Font("System", 11));
      heightField.setPrefColumnCount(8);
      GridPane.setConstraints(heightField, 1, 1, 1, 1, HPos.LEFT, VPos.BOTTOM);

      gp2.getChildren().addAll(widthLabel, widthField, // add to gridpane 2
          heightLabel, heightField);
      gp2.setPadding(new Insets(0.0, 20.0, 0.0, 20.0)); // set padding

      Label templateLabel = new Label("Template:");
      GridPane.setConstraints(templateLabel, 0, 0, 1, 1,
          HPos.LEFT, VPos.CENTER);

      ChoiceBox<String> templateChoice = new ChoiceBox<String>();
      templateChoice.getItems().addAll("640x480", "600x800", "1024x768",
          "1600x1200");
      templateChoice.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          // set dimensions to those of selected template
          String val = (templateChoice.getValue()).toString();
          String[] splitVal = val.split("x");
          widthField.setText(splitVal[0]); // set width from template
          heightField.setText(splitVal[1]); // set height from template
        }
      });
      GridPane.setConstraints(templateChoice, 1, 0, 1, 1,
          HPos.LEFT, VPos.BOTTOM);

      gp1.getChildren().addAll(templateLabel, templateChoice);

      Button okButton = new Button("OK");
      okButton.setDefaultButton(true); // ok gets enter key press
      okButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          try {
            resetEditor(
                Double.parseDouble(widthField.getText()),
                Double.parseDouble(heightField.getText()));
            toolManager.setSaveState(true);
            popupStage.hide(); // close popup
          } catch (NumberFormatException except) {
            showError("The entered height and width values are invalid");
          }
        }
      });
      Button cancelButton = new Button("Cancel");
      cancelButton.setCancelButton(true); // cancel gets 'esc' key press
      cancelButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          popupStage.hide(); // close new project popup
        }
      });

      HBox buttonBox = new HBox(cancelButton, okButton);
      buttonBox.setPadding(new Insets(5.0));
      buttonBox.setSpacing(7.5);
      buttonBox.setAlignment(Pos.CENTER_RIGHT); // right align buttons

      vb.getChildren().addAll(gp1, sizeLabel, gp2, buttonBox);

      popupStage.show(); // show popup
    }

    /**
     * Given a File object, reads an image into a canvas and adds the canvas
     * to the editor pane.
     */
    private final void loadImage(File imageFile) {
      try {
        URL imageURL = (imageFile.toURI()).toURL();
        Image image = new Image(imageURL.toString());
        double height = image.getHeight();
        double width = image.getWidth();

        editorPane.getChildren().remove(canvas); //remove old canvas
        canvas = new Canvas(width, height);
        editorPane.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0.0, 0.0); // draw image onto canvas
        toolManager.setSaveState(false); // changes have occurred
      } catch (Exception except) {
        showError("An error occurred while trying to read in your image.");
      }
    }

    /**
     * Clears all scene graph objects from the editor pane
     * and creates a new canvas with the specified dimensions
     *
     * @param     width - the width of the new canvas
     * @param     height - the height of the new canvas
     */
    private void resetEditor(double width, double height) {
      editorPane.getChildren().removeAll(editorPane.getChildren());
      canvas = new Canvas(width, height);
      gc = canvas.getGraphicsContext2D();
      editorPane.getChildren().add(canvas);

      editorPane.setStyle("-fx-background-style: solid");

      toolManager.setEditor(editorPane); // sets the node shapes are drawn to
      toolManager.setSaveState(true); // no unsaved changes
    }

    /**
     * Sets the editorPane to a fixed width and height supplied as arguments
     * to the method
     *
     * @param   width - the desired width of the editor pane.
     * @param   height - the desired height of the editor pane.
     */
    public final void fixEditorSize(double width, double height) {
      editorPane.setMaxWidth(width);
      editorPane.setMinWidth(width);

      editorPane.setMaxHeight(height);
      editorPane.setMinHeight(height);
    }

    /**
     * Shows a popup window with the supplied error message and an "OK" button.
     *
     * @param s     Explanation of the error
     */
    private void showError(String s) {
      VBox vb = new VBox();
      popupScene = new Scene(vb, 120.0, 200.0);
      popupStage.setScene(popupScene);

      Button okButton = new Button("OK");
      okButton.setDefaultButton(true); // ok gets enter key press
      okButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          popupStage.hide();
        }
      });

      vb.getChildren().addAll(
          new Text(s), // new text node from error string
          okButton);

      popupStage.show(); // show popup
    }

    /**
     * Ask the ToolManager whether there are any unsaved changes in the
     * editorPane.
     * If there are, checkSaveState displays a popup asking the user to
     * confirm that they intend to execute whatever action they have started
     * and returns false.
     * <p>
     * If there are no unsaved changes, returns true.
     *
     * @param       e - the EventHandler that will be executed if
     *              by the 'OK' button on the popup that confirms whatever
     *              action the user has initiated.
     * @return      true if all changes were saved, false if there were unsaved
     *              changes and the popup appeared.
     */
    private final boolean checkSaveState(EventHandler<ActionEvent> e) {
      if (!toolManager.getSaveState()) {
        VBox vb = new VBox();
        vb.setSpacing(7.5);
        vb.setPadding(new Insets(10.0));
        vb.setAlignment(Pos.CENTER);

        popupScene = new Scene(vb, 220.0, 100.0);
        popupStage.setScene(popupScene);

        Text t1 = new Text("There are unsaved changes.");
        t1.setFont(Font.font("System", FontWeight.BOLD, 12));
        Text t2 = new Text("Are you sure?");
        t2.setFont(new Font("System", 12));

        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e); // set yes button action

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true); // gets 'esc' presses
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) { popupStage.hide(); }
        });

        HBox buttonBox = new HBox(cancelButton, yesButton);
        buttonBox.setPadding(new Insets(5.0));
        buttonBox.setSpacing(7.5);
        buttonBox.setAlignment(Pos.CENTER); // right align buttons

        vb.getChildren().addAll(t1, t2, buttonBox);

        popupStage.show();

        return false; // there were unsaved changes - don't proceed.
      } else
        return true; // no unsaved changes - proceed
    }

    public static void main(String [] args) { launch(args); }
}
