package layer;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import java.io.File;

public class LayerManager extends Stage {

    private Stage layerStage; // window containing layer manager
    private Scene root;

    private int layerCount; // counts the number of layers

	public void newImageLayer(File n) {
	    
    }
    public WritableImage getExport() {
	return new WritableImage(1,1)
	    }
}
	

