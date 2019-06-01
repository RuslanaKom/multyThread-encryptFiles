package MultyThread2.fileencrypt;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.databind.node.TextNode;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class App extends Application
{
	@Override
	public void start(Stage primaryStage) {
		try {
		    TextField folderText = new TextField();
		    TextField passwordText = new TextField();
		    TextField resultText = new TextField();
		    Button encButton = new Button("Encrypt");
		    Button decButton = new Button("Decrypt");
		    
		    Label labelFolder = new Label("Directory:");
            HBox hb1 = new HBox();
            hb1.getChildren().addAll(labelFolder, folderText);
            hb1.setSpacing(10);
            
		    Label labelPassword = new Label("password:");
            HBox hb2 = new HBox();
            hb2.getChildren().addAll(labelPassword, passwordText);
            hb2.setSpacing(10);

		    GridPane gridPane = new GridPane();
            gridPane.setVgap(5); 
            gridPane.setHgap(5);
            gridPane.setAlignment(Pos.TOP_CENTER); 
            
            gridPane.add(hb1, 0, 1);
            gridPane.add(hb2, 2, 1);
            gridPane.add(encButton, 4, 2);
            gridPane.add(decButton, 6, 2);
            gridPane.add(resultText, 0, 3);
            
            FolderWorker2 folderWorker = new FolderWorker2();
            
		    EventHandler<ActionEvent> encButtonHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                 String folderName = folderText.getText();
                 String password= passwordText.getText();
                	try {
						folderWorker.encryptFolder(folderName, password);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
                	resultText.setText("Encrypted");
                }
            };
            
		    EventHandler<ActionEvent> decButtonHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                 String folderName = folderText.getText();
                 String password= passwordText.getText();
                	try {
						folderWorker.decryptFolder(folderName, password);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
                	resultText.setText("Decrypted");
                }
            };

            encButton.setOnAction(encButtonHandler);
            decButton.setOnAction(decButtonHandler);
            
            Scene scene = new Scene(gridPane,800,200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("ENCRYPTER");
            primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
