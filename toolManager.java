package draw;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.event.Event;

/* ShapeDrawer: provides a consolidated interface for draw operations */
public final class Artist {

  private Color primaryColor;
  private Color secondaryColor;
  
  public ToolManager() {
    /* set bw as default colors */
    primaryColor = Color.BLACK;
    secondaryColor = Color.WHITE;
  }

  /* set active color */
  public final void setColor(Color c) {
    primaryColor = c;
  }
}

