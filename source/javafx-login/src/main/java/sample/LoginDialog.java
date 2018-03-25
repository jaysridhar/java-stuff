package sample;

/*
 * Copyright 2017 Jay Sridhar
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @Author Jay Sridhar
 */

import java.util.Optional;
import javafx.util.Pair;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;

public class LoginDialog extends Application
{
    private Parent makeContents()
    {
	DialogPane root = new DialogPane();
	root.setHeaderText("Enter Credentials");
	root.setGraphic(new ImageView(getClass().getResource("/images/key.png").toString()));
	root.setPadding(new Insets(10, 30, 10, 30));
	root.getStylesheets().add(getClass().getResource("/css/my.css").toString());

	ButtonType okButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
	root.getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	((Button)root.lookupButton(ButtonType.CANCEL)).setOnAction(event -> Platform.exit());
	Button button = ((Button)root.lookupButton(okButton));

	GridPane grid = new GridPane();
	grid.setHgap(10);
	grid.setVgap(10);
	grid.setPadding(new Insets(10, 40, 10, 40));

	TextField username = new TextField();
	username.setPromptText("Username");
	username.setPrefWidth(300);
	GridPane.setHgrow(username, Priority.ALWAYS);

	PasswordField password = new PasswordField();
	password.setPromptText("Password");
	GridPane.setHgrow(password, Priority.ALWAYS);

	ChangeListener<String> cl = (obs, ov, nv) -> {
	    button.setDisable(username.textProperty().getValue().isEmpty() ||
			      password.textProperty().getValue().isEmpty() );
	};

	username.textProperty().addListener(cl);
	password.textProperty().addListener(cl);

	{
	    Node node = new Label("Username:");
	    GridPane.setHalignment(node, HPos.RIGHT);
	    grid.add(node, 0, 0);
	}

	grid.add(username, 1, 0);

	{
	    Node node = new Label("Password:");
	    GridPane.setHalignment(node, HPos.RIGHT);
	    grid.add(node, 0, 1);
	}

	grid.add(password, 1, 1);

	root.setContent(grid);

	{
	    button.setDisable(true);
	    button.setOnAction(e -> {
		    String uname = username.getText();
		    String pwd = password.getText();
		    System.out.println("Login with: " + uname + ", " + pwd);
		    Platform.exit();
		});
	}

	Platform.runLater(() -> username.requestFocus());

	return root;
    }

    @Override
    public void start(Stage stage) throws Exception
    {
	Scene scene = new Scene(makeContents());
	stage.resizableProperty().setValue(Boolean.FALSE);
	stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/key.png")));
	stage.setTitle("Application Login"); 
        stage.setScene(scene);
        stage.show();
    }

    static public void main(String[] args) throws Exception
    {
	Application.launch(args);
    }
}
