package draw.tool;

import javafx.scene.shape.Shape;
import javafx.scene.paint.Paint;

public interface Tool {

  // init: init shape to cursor location
  Shape init(double xcoordinate, double ycoordinate);

  // setStart: re-set the initial location of the shape
  void setStart(double xcoordinate, double ycoordinate);

  // draw: draw shape from start location to cursor location
  void draw(double xcoordinate, double ycoordinate);

  // setShape: sets the shape under edit
  void setShape(Shape s);
}
