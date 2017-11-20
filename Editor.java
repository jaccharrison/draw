package draw;

import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.event.*;
import java.util.Stack;

/* Editor: contains methods for drawing shapes and adjusting images */
public final class Editor extends SubScene {

  private Color activeColor; //drawn items will be this color

  private Pane root;

  private Line line;
  private Rectangle rectangle;

  public Editor(Pane r, double width, double height) {
    super(r, width, height);
    root = r;
  }

  public Color getActiveColor() {
    return activeColor;
  }

  public void setActiveColor(Color c) {
    activeColor = c;
  }

  /* setLineTool: sets the active tool to draw lines */
  public void setLineTool() {
    setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        line = new Line(e.getX(), e.getY(), e.getX(), e.getY());
        line.setStroke(activeColor);
        root.getChildren().add(line);
      }
    });

    setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        line.setEndX(e.getX());
        line.setEndY(e.getY());
      }
    });
  }

  /* setRectTool: sets the active tool to draw rectangles */
  public void setRectTool() {
    setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        rectangle = new Rectangle(e.getX(), e.getY(), 0.0, 0.0);
        root.getChildren().add(rectangle);
      }
    });

    setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        double w = e.getX() - rectangle.getX();
        double h = e.getY() - rectangle.getY();
        if (w < 0 || h < 0) { //TODO: FIX
          double origX = rectangle.getX(); //save original position of up-left
          double origY = rectangle.getY();
          rectangle.setX(e.getX()); //redraw with upper left corner at mouse
          rectangle.setY(e.getY());
          rectangle.setWidth(-w); //adjust width and height
          rectangle.setHeight(-h);
        } else {
          rectangle.setWidth(e.getX() - rectangle.getX()); //compute width
          rectangle.setHeight(e.getY() - rectangle.getX()); //compute height
        }
      }
    });
  }
}







