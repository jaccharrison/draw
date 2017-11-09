package draw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.BorderPane;
import javafx.event.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javax.imageio.ImageIO;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;
import javax.imageio.ImageIO;

import draw.stage.layer.LayerManager;

public final class DrawApp extends Application {

  private Stage primaryStage; //main window
  private Scene primaryScene;
  private BorderPane root; //organizes main stage

  private MenuBar menuBar;
  private LayerManager layerManager;

  private Boolean saveState; //true if no unsaved changes

  /* start: build interface */
  public final void start(Stage primaryStage) {

    primaryStage.setTitle("Draw");
    primaryStage.setMinHeight(100.0);
    primaryStage.setMinWidth(150.0);

    /* building blocks of window */
    root = new BorderPane();
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
            layerManager.newLayer(new Image(imgFile.toURI().toURL().toString()));
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
       if (save != null) {
         try {
           ImageIO.write(SwingFXUtils.fromFXImage(layerManager.getExport(),
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

    menuBar = new MenuBar(fileMenu); // compile menubar
  }

  /* Main: not used. ensures compatibilty with swing apps */
  public static void main(String [] args) {
    launch(args);
  }
}
