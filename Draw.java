package draw;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.net.URI;
import java.net.URL;
import java.lang.String;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Draw extends Application {
    
    protected boolean saveState = true; // changed since last save?
    
    @Override
    public void start(Stage imageStage) {
	
	/* Create canvas */
	final Canvas canvas = new Canvas(1080, 720);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	
	/* Create Menus */
	final Menu mFile = new Menu("File");
	// File menu items:
	MenuItem iOpen = new MenuItem("Open Image");
	iOpen.setOnAction(new EventHandler<ActionEvent>() {
		
		@Override
		public void handle(ActionEvent e) {

		    FileChooser fc = new FileChooser();
		    fc.setTitle("Select image");
		    fc.getExtensionFilters().addAll(
			new ExtensionFilter("Image Files", "*.png", "*.jpg",
					    "*.gif", "*.bmp"),
			new ExtensionFilter("All Files", "*.*")
		    );
		    java.io.File imgFile = fc.showOpenDialog(imageStage);

		    try {
			URI imgURI = imgFile.toURI();
			URL imgURL = imgURI.toURL();
			Image img = new Image(imgURL.toString());
			canvas.setHeight(img.getHeight());
			canvas.setWidth(img.getWidth());
			gc.drawImage(img, 0, 0);
		    } catch(Exception h) {
			System.out.println("Image load failed or cancelled");
		    }
		};
	    });
	
	MenuItem iExp = new MenuItem("Export as Image");
	mFile.getItems().addAll(iOpen, iExp);

	/* Create MenuBar */
	MenuBar menuBar = new MenuBar(mFile);
	
	/* Create scene for image */
	VBox imageBox = new VBox(menuBar, canvas);
	Scene imageScene = new Scene(imageBox);
	imageStage.setScene(imageScene);

	imageStage.show();

    }

    public static void main(String [] args) {
	launch(args);
    }
}

	
	    
				       
