package draw.tool;

import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Paint;

public final class TextTool implements Tool {

  private Text text;
  private String userText = ""; // holds the text string

  // init: create new text area with user-defined text
  public Text init(double x, double y) {
    text = new Text(x, y, userText);
    return text;
  }

  // setStart: re-position text node
  public void setStart(double x, double y) {
    text.setX(x);
    text.setY(y);
  }

  // draw: text has no 'draw' operation
  public void draw(double x, double y) {
    return;
  }

  // setShape: set text node under edit
  public void setShape(Shape t) {
    text = (Text) t;
  }

  // getUserText: used by DrawApp to retrieve the last text string entered
  public String getUserText() {
    return userText;
  }

  public void setUserText(String t) {
    userText = t;
  }

  // updateText: updates text in node to match last entered userText
  public void updateText() {
    text.setText(userText);
  }
}
