package draw.tool;

import javafx.scene.layout.Pane;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import javafx.event.*;
import javafx.scene.input.MouseEvent;

public final class ToolManager {

  private Pane editor; // node where drawing actions take place

  private Paint activePaint; // active stroke color
  private double activeWidth; // active stroke width

  private Shape shape; // shape under edit

  // public tools - used by DrawApp to set the active tool
  public static final LineTool lineTool = new LineTool();
  public static final RectangleTool rectangleTool = new RectangleTool();
  public static final PencilTool pencilTool = new PencilTool();

  public ToolManager(Pane p) {
    editor = p; //set object under edit
    activeWidth = 1.0; //init width to 1.0
    activePaint = Color.BLACK; //init color to black
  }

  public Paint getActivePaint() {
    return activePaint;
  }

  public double getActiveWidth() {
    return activeWidth;
  }

  public void setActivePaint(Paint c) {
    activePaint = c;
  }

  public void setActiveWidth(double w) {
    activeWidth = w;
  }

  // setTool: set the onMousePressed and onMouseDragged events on editor
  public void setTool(Tool t) {
    editor.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        shape = t.init(e.getX(), e.getY());
        shape.setStroke(activePaint); // set stroke color
        shape.setStrokeWidth(activeWidth); // set stroke width
        editor.getChildren().add(shape); // cast to shape and add to scene
      }
    });
    editor.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) { t.draw(e.getX(), e.getY()); }
    });
  }
}
