package draw.tool;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Paint;

public final class RectangleTool implements Tool {

  private class DrawRectangle extends Rectangle {
    private double initX; private double initY; // initial upper-left corner
    DrawRectangle(double x, double y, double w, double h) {
      super(x, y, w, h);
      initX = x;
      initY = y;
    }
  }

  private DrawRectangle rect;

  // init: init upper-left corner to cursor location, width and height = 0
  public Rectangle init(double x, double y) {
    rect = new DrawRectangle(x, y, 0.0, 0.0) {
      double initX = x; // store initial x
      double initY = y; // store initial y
    };
    return rect;
  }

  // setStart: set a different upper-left corner for the rectangle
  public void setStart(double x, double y) {
    rect.setX(x);
    rect.setY(y);
  }

  // draw: draw rectangle from initial location to cursor
  public void draw(double x, double y) {
    double w = x - rect.initX;
    double h = y - rect.initY;
    if (w < 0) { // cursor is left of initial upper-left corner
      rect.setX(x); // set new upper-left x-coordinate
      rect.setWidth(-w); // set width negative relative to initial up-left
    } else { rect.setWidth(w); } // set new positive width

    if (h < 0) { // cursor is above initial upper-left corner
      rect.setY(y); // set new upper-left y-coordinate
      rect.setHeight(-h); // set height negative relative to initial up-left corner
    } else { rect.setHeight(h); } //set new positive height
  }

  // setShape: set rectangle under edit
  public void setShape(Shape r) {
    rect = (DrawRectangle) r;
  }
}
