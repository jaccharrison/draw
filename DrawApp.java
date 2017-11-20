package draw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.SnapshotParameters;
import javafx.event.*;
import javafx.scene.control.*;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;

public final class DrawApp extends Application {

  private Stage drawStage; //main window
  private Scene drawScene;
  private BorderPane root; //organizes main stage

  private MenuBar menuBar;
  private Editor editor;

  private Canvas canvas;
  private Pane canvasPane;
  private Pane toolPane;
  private GraphicsContext gc;

  private final ColorPicker primaryColorPicker = new ColorPicker();
  private final ColorPicker secondaryColorPicker = new ColorPicker();

  /* start: build UI */
  public final void start(Stage drawStage) {

    drawStage.setTitle("Draw");
    drawStage.setMinHeight(200.0);
    drawStage.setMinWidth(250.0);

    root = new BorderPane();
    drawScene = new Scene(root, 500.0, 500.0);
    drawStage.setScene(drawScene);

    /* add menu */
    buildMenuBar();
    root.setTop(menuBar); //add menu to top of scene

    /* add canvas */
    canvas = new Canvas(400.0, 400.0);
    canvasPane = new Pane(canvas);
    gc = canvas.getGraphicsContext2D();

    /* add editor */
    editor = new Editor(canvasPane, 400.0, 400.0);
    root.setCenter(editor);

    /* add tool pane */
    buildToolPane();

    drawStage.sizeToScene();
    drawStage.show(); // show window
  }

  /* buildMenu: build MenuBar and register EventHandlers */
  private final void buildMenuBar() {

    final Menu fileMenu = new Menu("File");
    final Menu toolMenu = new Menu("Tools");

    // File menu items
    MenuItem openItem = new MenuItem("Open Image");
    openItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
            new ExtensionFilter("All Files", "*.*"));
        /* get File to be opened */
        File imgFile = fileChooser.showOpenDialog(drawStage);
        if (imgFile != null) {
          try {
            Image img = new Image(imgFile.toURI().toURL().toString());
            double imgWidth = img.getWidth();
            double imgHeight = img.getHeight();
            canvas.setWidth(imgWidth);
            canvas.setHeight(imgHeight);
            gc.drawImage(img, 0.0, 0.0);
          } catch (Exception ex) {}
        }
      }
    });

    MenuItem exportItem = new MenuItem("Export");
    exportItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("PNG", "*.png"));
        File save = fileChooser.showSaveDialog(drawStage);
        if (save != null) {
          try {
            ImageIO.write(SwingFXUtils.fromFXImage(editor.snapshot(new SnapshotParameters(), null),
                  null), "png", save);
          } catch (Exception ex) {}
        }
      }
    });
    fileMenu.getItems().addAll(openItem, exportItem); //add items to file menu

    // Tools menu items
    MenuItem lineItem = new MenuItem("Line");
    lineItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) { editor.setLineTool(); }
    });
    MenuItem rectItem = new MenuItem("Rectangle");
    rectItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) { editor.setRectTool(); }
    });
    toolMenu.getItems().addAll(lineItem); //add items to tool menu

    menuBar = new MenuBar(fileMenu, toolMenu); // compile menubar
  }

  /* buildToolPane: constructs tool pane used to draw objects and adjust img */
  private final void buildToolPane() {
    toolPane = new Pane();
    toolPane.getChildren().add(primaryColorPicker);

    primaryColorPicker.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        editor.setActiveColor(primaryColorPicker.getValue());
      }
    });
    root.setLeft(toolPane);
  }

  public static void main(String [] args) {
    launch(args);
  }
}

