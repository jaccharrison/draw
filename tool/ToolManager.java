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

  private boolean saved = true; // track whether there are unsaved changes

  // public tools - used by DrawApp to set the active tool
  public static final LineTool lineTool = new LineTool();
  public static final RectangleTool rectangleTool = new RectangleTool();
  public static final PencilTool pencilTool = new PencilTool();
  public static final TextTool textTool = new TextTool();

  public ToolManager(Pane p) {
    editor = p; //set object under edit
    activeWidth = 1.0; //init width to 1.0
    activePaint = Color.BLACK; //init color to black
    setTool(lineTool); //default to lineTool
  }

  public Paint getActivePaint() {
    return activePaint;
  }

  public double getActiveWidth() {
    return activeWidth;
  }

  public Pane getEditor() {
    return editor;
  }

  public boolean getSaveState() {
    return saved;
  }

  public void setActivePaint(Paint c) {
    activePaint = c;
  }

  public void setActiveWidth(double w) {
    activeWidth = w;
  }

  public void setEditor(Pane e) {
    editor = e;
  }

  /* resetSaveState: set saved to true - all changes saved */
  public void setSaveState(boolean b) {
    saved = b;
  }

  // setTool: set the onMousePressed and onMouseDragged events on editor
  public void setTool(Tool t) {
    editor.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        saved = false; // there are unsaved changes
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

    editor.setOnMouseDragExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) {
        editor.setOnMouseDragged(null); // hold drawing where mouse exited
        t.draw(e.getX(), e.getY());
      }
    });
    editor.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent e) { // re-register drag event
        editor.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent f) { t.draw(f.getX(), f.getY()); }
        });
      }
    });
  }
}
