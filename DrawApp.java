package draw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.event.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
/* imports for image io */
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;
import javax.imageio.ImageIO;
import javafx.scene.SubScene;

import javafx.scene.SnapshotParameters;
import draw.stage.layer.LayerManager;
import draw.stage.layer.Layer;

public final class DrawApp extends Application {

  private Stage primaryStage; //main window
  private Scene primaryScene;
  private BorderPane root; //organizes main stage

  private SubScene editor; //subscene for easel 
  private StackPane easel; //holds canvases under edit

  private MenuBar menuBar; //control center for application

  private LayerManager layerManager;
  private S

  private Boolean saveState; //true if no unsaved changes

  /* start: build interface */
  public final void start(Stage primaryStage) {

    primaryStage.setTitle("Draw");
    primaryStage.setMinHeight(100.0);
    primaryStage.setMinWidth(150.0);

    /* building blocks of window */
    root = new BorderPane();
    easel = new StackPane();
    root.setCenter(easel);
    primaryScene = new Scene(root);
    primaryStage.setScene(primaryScene);

    /* add menu */
    buildMenu();
    root.setTop(menuBar); //add menu to top of scene

    /* instantiate layerManager */
    layerManager = new LayerManager(primaryStage);

    primaryStage.show(); // show window
  }

  /* buildMenu: build MenuBar with EventHandlers */
  private final void buildMenu() {

    /* 'file' menu */
    final Menu fileMenu = new Menu("File"); //menu entry
    MenuItem newItem = new MenuItem("New Project"); //File > New Project
    newItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
      }
    });
    MenuItem openItem = new MenuItem("Open Image"); //File > Open Image
    openItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      /* select and open image from filesystem as project layer */
      public void handle(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
            new ExtensionFilter("All Files", "*.*"));
        /* get File to be opened */
        File imgFile = fileChooser.showOpenDialog(primaryStage);
        if (imgFile != null) {
          try {
            /* convert to Image and send to layerManager */ 
            Layer nl = 
              layerManager.newLayer(new Image(imgFile.toURI().toURL().toString()),
                  "Test");
            easel.getChildren().add(nl);
          } catch (Exception ex) {} //TODO: implement catch
        }
      }
    });
    MenuItem exportItem = new MenuItem("Export"); //File > Export...
    exportItem.setOnAction(new EventHandler<ActionEvent>() {
     @Override
     public void handle(ActionEvent e) {
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Save Image");
       fileChooser.getExtensionFilters().addAll(
           // TODO: implement different filetype exports
//           new ExtensionFilter("JPEG", "*.jpg");
           new ExtensionFilter("PNG", "*.png"));
//           new ExtensionFilter("GIF", "*.gif");
//           new ExtensionFilter("BMP", "*.bmp"));
       File save = fileChooser.showSaveDialog(primaryStage);
       WritableImage export = easel.snapshot(new SnapshotParameters(), null);
       if (save != null) {
         try {
           ImageIO.write(SwingFXUtils.fromFXImage(export,
                 null), "png", save);
         } catch (Exception ex) {} //TODO: impelement catch
       }
     }
    });
    MenuItem closeItem = new MenuItem("Quit"); //File > Quit
    closeItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        if (saveState)
          primaryStage.close();
      }
    });
    /* add items to file menu */
    fileMenu.getItems().addAll(newItem, openItem, exportItem, closeItem);

    /* 'edit' menu */
/*    final Menu editMenu = new Menu("Edit");
    MenuItem cpItem = new MenuItem("Copy");
    MenuItem cutItem = new MenuItem("Cut");
    MenuItem pItem = new MenuItem("Paste");
    editMenu.getItems().addAll(cpItem, cutItem, pItem); */

    /* 'layer' menu */
    final Menu layerMenu = new Menu("Layers");
    MenuItem newLayerItem = new MenuItem("New Layer");
    newLayerItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {

      }
    });
    MenuItem rmLayerItem = new MenuItem("Remove Layer");

    menuBar = new MenuBar(fileMenu); // compile menubar
  }

  /* Main: not used. ensures compatibilty with swing apps */
  public static void main(String [] args) {
    launch(args);
  }
}
