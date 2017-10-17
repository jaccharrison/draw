package draw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.event.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.canvas.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

public class DrawApp extends Application {

    private Stage imageStage;
    private Scene imageScene;
    private VBox imageBox = new VBox();

    private PixelReader pixelReader;
    private PixelWriter pixelWriter;
    private Image img;
    private WritableImage wImage;
    private ImageView iv = new ImageView();

    private GraphicsContext gc;

    private EventHandler openImage = new EventHandler<ActionEvent>() {
	    
	@Override
	public void handle(ActionEvent e) {
	    FileChooser fc = new FileChooser();
	    fc.setTitle("Select Image");
	    fc.getExtensionFilters().addAll(
	        new ExtensionFilter("Image Files", "*.png", "*.jpg",
				    "*.gif", "*.bmp"),
		new ExtensionFilter("All Files", "*.*")
	    );
	    File imgFile = fc.showOpenDialog(imageStage);
	    try {
		img = new Image(imgFile.toURI().toURL().toString());
		pixelReader = img.getPixelReader();
		iv.setImage(img);
		imageBox.getChildren().add(iv);
	    } catch(Exception h) {
		System.out.println("Image load failed or cancelled");
	    }
	};
    };

    private EventHandler exportImage = new EventHandler<ActionEvent>() {

	@Override
	public void handle(ActionEvent e) {
	    FileChooser fc = new FileChooser();
	    fc.setTitle("Select Output File");
	    File saveFile = fc.showSaveDialog(imageStage);

	    WritableImage wImage = imageScene.snapshot(null);
	    
	    try {
		ImageIO.write(SwingFXUtils.fromFXImage(wImage, null),
			      "png", saveFile);
	    } catch (Exception ex) {
		System.out.println(ex.getMessage());
	    }
	};
    };

    private EventHandler newProject = new EventHandler<ActionEvent>() {
	    // TODO: Implement Layer management window
	    @Override
	    public void handle(ActionEvent e) {
		final Canvas canvas = new Canvas();
		gc = canvas.getGraphicsContext2D();
		imageBox.getChildren().add(canvas);
	    };
	};

    private EventHandler close = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		imageStage.close();
	    }
	};
    
    public void start(Stage imageStage) {
	imageStage.setTitle("Draw");
	imageStage.setMinHeight(300.0);
	imageStage.setMinWidth(400.0);
	
	/* File menu */
	MenuItem newItem = new MenuItem("New Project");
	MenuItem openItem = new MenuItem("Open Image");
	MenuItem exportItem = new MenuItem("Export Image");
	MenuItem closeItem = new MenuItem("Close");
	
	final Menu fileMenu = new Menu("File");
	fileMenu.getItems().addAll(newItem, openItem, exportItem, closeItem);

	MenuBar menuBar = new MenuBar(fileMenu);
	imageBox.getChildren().add(menuBar);
	imageScene = new Scene(imageBox);
	imageStage.setScene(imageScene);
	imageStage.show();

	openItem.setOnAction(openImage);
	exportItem.setOnAction(exportImage);
    }
		
    
    public static void main(String [] args) {
	launch(args);
    }
}

	
	    
				       
