package draw.tool;

import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.paint.Paint;

public final class PencilTool implements Tool {

  private Path path;

  // init: init path to cursor location
  public Path init(double x, double y) {
    path = new Path(new MoveTo(x,y));
    return path;
  }

  // setStart: set a different start point for the drawing
  public void setStart(double x, double y) {
    path.getElements().add(new MoveTo(x,y)); //TODO: this is bullshit. fix.
  }

  // draw: freehand draw with pencil
  public void draw(double x, double y) {
    path.getElements().add(new LineTo(x, y));
  }

  // setShape: sets the path under edit
  public void setShape(Shape p) {
    path = (Path) p;
  }
}
