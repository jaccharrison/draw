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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.lang.String;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

import layer.LayerManager;

public static class DrawApp extends Application {

	/* components of main stage */
	private Stage primaryStage; // main editing window
	private Scene primaryScene;
	private BorderPane root; // organizes main stage
	private MenuBar menuBar;

	private LayerManager layerManager;

	private Boolean saveState; // false if changes have occurred since save

	/* EVENT HANDLERS FOR MENUS */

	// openImage: displays filechooser and creates a new layer form image
	private EventHandler openImage = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			FileChooser fc = new FileChooser();
			fc.setTitle("Select Image");

			File imgFile = fc.showOpenDialog(primaryStage); // get file

			try { // attempt to read into new layer 
				layerManager.newImageLayer(imgFile);
			} catch(Exception h) {
				System.out.println("Image load failed or cancelled");
			}
		}
	};

	// exportImage: display filechooser and write to new image file
	private EventHandler exportImage = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			FileChooser fc = new FileChooser();
			fc.setTitle("Select Destination File");
			File saveFile = fc.showSaveDialog(primaryStage);

			WritableImage wImage = layerManager.getExport();

			try {
				ImageIO.write(SwingFXUtils.fromFXImage(wImage, null),
						"png", saveFile);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	};
	
	// close: check if saved and close project
	private EventHandler close = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent e) {
			if (!saveState) {
				Popup pop = new Popup();
				Text t = new Text();
				t.setText("There are unsaved changes.\nAre you sure you want to quit?");
				t.setTextAlignment(TextAlignment.CENTER);	
				StackPane text = new StackPane();
				text.getChildren().add(t);
				Scene scene = new Scene(text);	
				pop.show(primaryStage);
			}
			primaryStage.close();
		}
	};

	/* start: start the application. build windows */
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Draw");
		primaryStage.setMinHeight(100.0);
		primaryStage.setMinWidth(100.0);

		/* CREATE MENUBAR */ 

		/* 'file' menu */
		final Menu fileMenu = new Menu("File");
		MenuItem newItem = new MenuItem("Create Project");
		MenuItem openItem = new MenuItem("Open Image");
		openItem.setOnAction(openImage); // set open action
		MenuItem exportItem = new MenuItem("Export Image");
		exportItem.setOnAction(exportImage); // set export action
		MenuItem closeItem = new MenuItem("Quit");
		fileMenu.getItems().addAll(newItem, openItem, exportItem, closeItem);

		/* 'edit' menu */
		final Menu editMenu = new Menu("Edit");
		MenuItem cpItem = new MenuItem("Copy");
		MenuItem cutItem = new MenuItem("Cut");
		MenuItem pasteItem = new MenuItem("Paste");
		editMenu.getItems().addAll(cpItem, cutItem, pasteItem);

		menuBar = new MenuBar(fileMenu, editMenu); // compile menubar

		/* CREATE EDITING CANVAS */

		root.setTop(menuBar);
		primaryScene = new Scene(root);
		primaryStage.setScene(primaryScene);
		primaryStage.show();
	}

	/* Main: not used. ensures compatibilty with swing apps */
	public static void main(String [] args) {
		launch(args);
	}
}




