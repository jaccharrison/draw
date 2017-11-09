package draw;

import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;

public final class Editor extends SubScene {

  private StackPane easel; //stackpane of layers
  private LayerManager layerManager;

  private gcUnderEdit;

  public Editor(Parent root, double width, double height) {
    super(root, width, height);

    layerManager = new LayerManager();
    gcUnderEdit = layerManger.addLayer(width, height).getGraphicsContext2D();


  }
