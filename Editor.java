package draw;

import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.scene.shape.PathElement.*;
import javafx.event.*;
import java.util.Stack;

/* Editor: contains methods for drawing shapes and adjusting images */
public final class Editor extends SubScene {

  private Color activeColor; //drawn items will be this color

  private Pane root;

  private Line line; //holds new lines
  private DrawRectangle rectangle; //holds new rectangles
  private Path path; //holds new paths

  private double x; //temporary storage for width/height calcs
  private double y;

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
        root.getChildren().add(line);
      }
    });
    setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        line.setStroke(activeColor); //set color
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
        //new 0w, 0h rectangle with upper left at cursor
        rectangle = new DrawRectangle(e.getX(), e.getY(), 0.0, 0.0);
        root.getChildren().add(rectangle); //add to scene graph
      }
    });
    setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        rectangle.setStroke(activeColor); //set color
        rectangle.setFill(activeColor);
        //find position of mouse relative to upper-left corner
        x = e.getX() - rectangle.getInitX(); 
        y = e.getY() - rectangle.getInitY();
        if (x < 0) { //x to left of upper-left corner
          rectangle.setX(e.getX());
          rectangle.setWidth(-x);
        } else
          rectangle.setWidth(x);

        if (y < 0) { //y above upper-left corner
          rectangle.setY(e.getY());
          rectangle.setHeight(-y);
        } else
          rectangle.setHeight(y);
      }
    });
  }

  /* setPencilTool: sets the active tool to draw using a pencil */
  public void setPencilTool() {
    setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        path = new Path(); //new path
        path.getElements().add(new MoveTo(e.getX(), e.getY())); //at cursor
        root.getChildren().add(path); //add to scene graph
      }
    });
    setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        path.setStroke(activeColor); //set color
        path.getElements().add(new LineTo(e.getX(), e.getY()));
      }
    });
  }
}







