package draw;

import javafx.collections.*;
import java.util.Map;
import java.lang.String;
import javafx.scene.image.*;
import javafx.scene.control.ListView;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import java.nio.ByteBuffer;

public final class LayerManager {

  private Map<String, Canvas> layerMap; //layer-name map
  private ListView<String> listView;

  private int layerCount;

  public LayerManager() {
    /* observable wrapper for map data */
    final ObservableMap<String, Canvas> obsLayerMap =
      FXCollections.observableMap(layerMap);

    /* create ListView from map keys */
    listView = new ListView<String>();
    listView.setPrefWidth(150);

    layerCount = 0;
  }

  /* getListView: return the listView associated with the layerManager */
  public final ListView<String> getListView() {
    return listView;
  }

  /* addLayer: create blank layer with user-specified dimensions */
  public final Canvas addLayer(double w, double h) throws Exception {
    Canvas newLayer = new Canvas(w, h);
    String name = "Unnamed " + layerCount++; //gen unique name
    if (layerMap.putIfAbsent(name, newLayer) != null) {
      throw new Exception(name);
    } else {
      return newLayer;
    }
  }

  /* addLayer: create blank named layer with user-spec dimensions */
  public final Canvas addLayer(double w, double h, String name) throws Exception {
    Canvas newLayer = new Canvas(w, h);
    if (layerMap.putIfAbsent(name, newLayer) != null) {
      throw new Exception(name);
    } else {
      return newLayer;
    }
  }

  /* addLayer: create new layer from image */
  public final Canvas addLayer(Image img, String name) throws Exception {
    Canvas newLayer = readImage(img);
    if (layerMap.putIfAbsent(name, newLayer) != null) {
      throw new Exception(name);
    } else {
      return newLayer;
    }
  }

  /* writeImage: returns Layer with pixel data from Image */
  private final Canvas readImage(Image img) {
    Canvas layer = new Canvas(img.getWidth(), img.getHeight());
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
