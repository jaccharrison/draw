package draw;

import javafx.scene.shape.Rectangle;

public final class DrawRectangle extends Rectangle {

  private double initialX;
  private double initialY;

  public DrawRectangle(double x, double y, double w, double h) {
    super(x, y, w, h);
    initialX = x;
    initialY = y;
  }

  public void setInitX(double x) {
    initialX = x;
  }

  public void setInitY(double y) {
    initialY = y;
  }

  public double getInitX() {
    return initialX;
  }

  public double getInitY() {
    return initialY;
  }

}
