package draw.stage.layer;

import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.image.*;
import java.io.File;
import javafx.scene.control.Button;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.canvas.GraphicsContext;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import javafx.collections.FXCollections;

public class LayerManager extends Stage {

  private static Stage layerStage; //layer manager window
  private static Scene layerScene;
  private static VBox root;

  private Stage owner; //ref to parent stage

  private ObservableList<Layer> layerList; //list of canvases
  private LinkedList<Layer> linkedList;
  private ListView lv; //graphical front to list

  /* constructor: create separate window, init list and toolbars */
  public LayerManager(Stage ownerWindow) {
    owner = ownerWindow; //store ref to owner

    layerStage = new Stage();
    root = new VBox();
    layerScene = new Scene(root);
    layerStage.setScene(layerScene);

    /* create/config stage window */
    layerStage.setTitle("Layers");
    layerStage.setMinHeight(100.0);
    layerStage.setMinWidth(100.0);

    linkedList = new LinkedList<Layer>();
    layerList = FXCollections.observableList(linkedList);

    lv = new ListView(layerList); // create listview of layers
    root.getChildren().add(lv);

    layerStage.show();
  }

  /* newLayer: create new layer with user-spec dimensions */
  public GraphicsContext newLayer(double x, double y) {
    Layer nl = new Layer(x, y); //create new layer
    layerList.add(nl); //add at end of layer list
    return nl.getGraphicsContext2D();
  }

  /* newLayer(Image): create new layer from image */
  public GraphicsContext newLayer(Image img) {
    Layer nl = new Layer(1.0, 1.0); //create layer
    nl.setHeight(img.getHeight()); //match image dimensions
    nl.setWidth(img.getWidth());

    /* load in image */
    // TODO: Optimize the way this reads pixel data
    PixelReader pixelReader = img.getPixelReader();
    GraphicsContext gc = nl.getGraphicsContext2D();
    PixelWriter pixelWriter = gc.getPixelWriter();
    PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
    // Read pixel color pixel by pixel into a canvas
    for (int i = 0; i < img.getHeight(); ++i) {
      for (int j = 0; j < img.getWidth(); ++j) {
        pixelWriter.setColor(i, j, pixelReader.getColor(i, j));
      }
    }
    layerList.add(nl);
    return nl.getGraphicsContext2D();
  }

  public WritableImage getExport() { //TODO: make this actually do something
    WritableImage wImage = new WritableImage(1,1);
    return wImage;
  }
}
