package draw.stage.layer;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.FXCollections;
import java.util.Map;
import javafx.scene.image.*;
import javafx.scene.canvas.GraphicsContext;
import java.nio.ByteBuffer;

public final class LayerManager {

  private final Map<String, Layer> layerMap; //layer-name map
  private ListView<String> listView;

  public final LayerManager() {
    /* observable wrapper for map data */
    final ObservableMap<String, Layer> observableLayerMap =
      FXCollections.observableMap(layerMap);

    /* create ListView from map keys */
    listView = new ListView<String>();
    listView.setPrefWidth(150);

    /* bind ListView and map */
    observableLayerMap.addListener(MapChangeListener<String,Layer> change -> {
      listView.getItems().removeAll(change.getKey());
      if (change.wasAdded()) {
        listView.getItems().add(change.getKey());
      }
    });
  }

  /* getListView: return the listView associated with the layerManager */
  public final ListView<String> getListView() {
    return listView;
  }

  /* addLayer: create blank layer with user-specified dimensions */
  public final Layer addLayer(double w, double h) {
    Layer newLayer = new Layer(w, h);
    String name = "Unnamed " + Layer.getLayerCount(); //gen unique name
    layerMap.put(name, newLayer);
    return newLayer;
  }

  /* addLayer: create blank named layer with user-spec dimensions */
  public final Layer addLayer(double w, double h, String name) {
    Layer newLayer = new Layer(w, h);
    if (layerMap.putIfAbsent(name, newLayer)) {
      throw new Exception(name);
    } else {
      return newLayer;
    }
  }

  /* addLayer: create new layer from image */
  public final Layer addLayer(Image img, String name) {
    Layer newLayer = readImage(Image img);
    if (layerMap.putIfAbsent(name, newLayer)) {
      throw new Exception(name);
    } else {
      return newLayer;
    }
  }

  /* writeImage: returns Layer with pixel data from Image */
  private final Layer readImage(Image img) {
    Layer layer = new Layer(img.getWidth(), img.getHeight());
    PixelReader pixelReader = img.getPixelReader();
    PixelWriter pixelWriter = layer.getGraphicsContext2D().getPixelWriter();
    PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
    // Read image pixel by pixel into a canvas
    for (int i = 0; i < img.getHeight(); ++i) {//TODO: optimize this
      for (int j = 0; j < img.getWidth(); ++j) {
        pixelWriter.setColor(i, j, pixelReader.getColor(i, j));
      }
    }
    return layer;
  }
}
