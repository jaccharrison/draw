package draw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.control.*;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;
import java.nio.ByteBuffer;

public final class DrawApp extends Application {

  private Stage drawStage; //main window
  private Scene drawScene;
  private BorderPane root; //organizes main stage

  private MenuBar menuBar;

  private Canvas canvas;
  private GraphicsContext gc;

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
    gc = canvas.getGraphicsContext2D();
    root.setCenter(canvas);

    drawStage.sizeToScene();
    drawStage.show(); // show window
  }

  /* buildMenu: build MenuBar and register EventHandlers */
  private final void buildMenuBar() {

    final Menu fileMenu = new Menu("File");
    final Menu drawMenu = new Menu("Draw");

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
            canvas.setHeight(img.getHeight());
            canvas.setWidth(img.getWidth());
            PixelReader pixelReader = img.getPixelReader();
            PixelWriter pixelWriter = gc.getPixelWriter();
            PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
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
            ImageIO.write(SwingFXUtils.fromFXImage(drawScene.snapshot(null),
                  null), "png", save);
          } catch (Exception ex) {} //TODO: impelement catch
        }
      }
    });

    /* add items to file menu */
    fileMenu.getItems().addAll(openItem, exportItem);

    menuBar = new MenuBar(fileMenu); // compile menubar
  }

  public static void main(String [] args) {
    launch(args);
  }
}
