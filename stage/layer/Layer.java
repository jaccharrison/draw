package draw.stage.layer;

import javafx.scene.canvas.*;
import javafx.scene.image.*;
import java.nio.ByteBuffer;

public class Layer extends Canvas {

    private static int layerCount = 0; //track number of layers
    private String name; //the layername

    private byte imageData[];

    private GraphicsContext gc; //graphics context for layer

    private int width;
    private int height;

    /* default constructor */
    public Layer() {
      super();
      gc = this.getGraphicsContext2D();
    }

    /* constructor with dimensions */
    public Layer(double w, double l) {
      super(w,l);
      gc = this.getGraphicsContext2D();
    }

    /* image constructor */
    public Layer(Image img) {
      super (img.getWidth(), img.getHeight());

      // this is a terrible way to get integers...
      width = (int)img.getWidth();
      height = (int)img.getHeight();

      imageData =
        new byte[width * height * 3];

      gc = this.getGraphicsContext2D();

      drawImageData(img); //read image into canvas
    }

    public void drawImageData(Image img) {
      //get pixel readers and writers 
      PixelReader pixelReader = img.getPixelReader(); //image pixelReader
      PixelWriter pixelWriter = gc.getPixelWriter(); //canvas pixelWriter

      //for now, only use rgb colorspace
      PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();

      //write from input img to canvas
      pixelWriter.setPixels(0, 0, width, height,
          pixelFormat, imageData, 0, width*3);
    }

    /* return the number of instantiated layers */
    public static final int getLayerCount() {
      return layerCount;
    }

    /* set the number of layers */
    public static final void setLayerCount(int c) {
      layerCount = c;
    }

    /* set the name of the layer */
    public void setName(String n) {
      name = n;
    }

    /* get the name of the layer */
    public String getName() {
      return name;
    }

}
