package draw.tool;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Paint;

public final class LineTool implements Tool {

  private Line line;

  // init: init line to cursor location
  public Line init(double x, double y) {
    line = new Line(x, y, x, y);
    return line;
  }

  // setStart: set a different starting point for the line
  public void setStart(double x, double y) {
    line.setStartX(x);
    line.setStartY(y);
  }

  // draw: draw line from initial location to cursor location
  public void draw(double x, double y) {
    line.setEndX(x);
    line.setEndY(y);
  }

  // setShape: set line under edit
  public void setShape(Shape l) {
    line = (Line) l;
  }
}
