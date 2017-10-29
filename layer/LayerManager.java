package layer;

import javafx.stage.Stage;
import javafx.stage.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import java.io.File;
import javafx.scene.control.Button;
import java.util.LinkedList;
public class LayerManager extends Stage {

  private static Stage layerStage = new Stage(); // window containing layer manager
  private static VBox root = new VBox();
  private static Scene layerScene = new Scene(root);

  private Stage owner;

  /* Toolbar and buttons */
  private static MenuBar toolbar = new MenuBar();
  private static Button addLayerButton = new Button("Add Layer");
  private static Button rmLayerButton = new Button("Delete Layer");
  private static Button upButton = new Button("Move Layer Up");
  private static Button downButton = new Button("Move Layer Down");
  private static Button mergeDownButton = new Button("Merge Layers Down");

  private int layerCount; // counts the number of layers

  private ObservableList<layer>; // list containing canvases

  public LayerManager(Stage ownerWindow) {
    owner = onwerWindow;

    /* create and configure stage */
    layerStage.setTitle("Layers");
    layerStage.setMinHeight(100.0);
    layerStage.setMinWidth(100.0);

    layerCount = 0; // no layers

    /* add toolbar and assign button actions */
    toolbar.getMenus().addAll(addLayer, delLayer, up, down, mergeDown);

    addLayerButton.setOnAction(new EventHandler<ActionEvent>() {
     
      @Override
      public void handle(ActionEvent e) {
        newLayer(0); // add blank canvas at top priority
      }
    });

    rmLayerButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent e) {
        rmLayer(getSelection()); // remove selected layers
      }
    });

    layers = new LinkedList<layer>(); // instantiate list of layers

    lv = new ListView(layers); // create listview of layers

    //TODO: Add other button actions
  }

  public void newLayer(void) {
    Layer nl = new Layer(); // create new layer
    nl.setHeight(h); // set height to height of project 
    nl.setWidth(h); // set width to project width

    layers.add(nl); // add at end of layer list
  }

  public int newLayer(Image img) {
    Layer nl = new Layer(); // create layer
    nl.setHeight(img.getHeight());
    nl.setWidth(img.getWidth());

    /* load in image */
    // TODO: Optimize the way this reads pixel data
    pixelReader = img.getPixelReader();
    GraphicsContext gc = nl.getGraphicsContext2D();
    PixelWriter pixelWriter = gc.getPixelWriter();
    PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
    // Read pixel color pixel by pixel into a canvas
    for (int i = 0; i < img.getHeight(); ++i) {
      for (int j = 0; j < img.getWidth(); ++j) {
        pixelWriter.setColor(i, j, pixelReader.getColor(i, j));
      }
    }
    layerCount++; // add layer
    layers.add(nl);
  }

  public WritableImage getExport() {
    wImage = new WritableImage
    return new WritableImage(1,1)
  }

}
