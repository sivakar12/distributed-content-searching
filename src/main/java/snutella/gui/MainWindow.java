package snutella.gui;

import com.sun.org.apache.xpath.internal.patterns.NodeTestFilter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import snutella.Node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;


public class MainWindow extends Application {
    Node node;
    @Override
    public void start(Stage primaryStage) {
        getInitialInformation(primaryStage);
    }

    private void getInitialInformation(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        TextField addressField = new TextField("localhost");
        int randomPort = new Random().nextInt(0xFFFF);
        TextField portField = new TextField(String.valueOf(randomPort));
        TextField bssAddressField = new TextField("localhost");
        TextField bssPortField = new TextField("55555");
        Button okButten = new Button("OK");
        okButten.setOnAction((event) -> {
            String address = addressField.getText();
            int port = Integer.parseInt(portField.getText());
            String bssAddress = bssAddressField.getText();
            int bssPort = Integer.parseInt(bssPortField.getText());

            this.node = new Node(address, port, bssAddress, bssPort);
            primaryStage.close();
        });

        gridPane.add(new Label("Address"), 0, 0);
        gridPane.add(new Label("Port"), 0, 1);
        gridPane.add(new Label("Bootstrap Server Address"), 0, 2);
        gridPane.add(new Label("Bootstrap Server Port"), 0, 3);
        gridPane.add(addressField, 1, 0);
        gridPane.add(portField, 1, 1);
        gridPane.add(bssAddressField, 1, 2);
        gridPane.add(bssPortField, 1, 3);
        gridPane.add(okButten, 1, 4);

        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}