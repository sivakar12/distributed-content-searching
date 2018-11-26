package snutella.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import snutella.Node;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class MainWindow extends Application {
    Node node;
    @Override
    public void start(Stage primaryStage) {
        getInitialInformation(primaryStage);
    }

    private Scene getPrimaryScene() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("node_details.fxml"));
        try {
            MainController controller = new MainController(this.node);
            loader.setController(controller);
            Pane mainPane = loader.<Pane>load();
            return new Scene(mainPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getInitialInformation(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        TextField addressField = new TextField("localhost");
        int randomPort = new Random().nextInt(0xFFFF);
        TextField portField = new TextField(String.valueOf(randomPort));
        TextField bssAddressField = new TextField("localhost");
        TextField bssPortField = new TextField("55555");

        String hostAddressFromEnv = System.getenv("SNUTELLA_HOST_ADDRESS");
        if (hostAddressFromEnv != null) {
            addressField.setText(hostAddressFromEnv);
        }
        String bssAddressFromEnv = System.getenv("SNUTELLA_BOOTSTRAP_SERVER_ADDRESS");
        if (bssAddressFromEnv != null) {
            bssAddressField.setText(bssAddressFromEnv);
        }
        String bssPortFromEnv = System.getenv("SNUTELLA_BOOTSTRAP_SERVER_PORT");
        if (bssPortFromEnv != null) {
            bssPortField.setText(bssPortFromEnv);
        }
        Button okButten = new Button("OK");
        okButten.setOnAction((event) -> {
            String address = addressField.getText();
            int port = Integer.parseInt(portField.getText());
            String bssAddress = bssAddressField.getText();
            int bssPort = Integer.parseInt(bssPortField.getText());

            this.node = new Node(address, port, bssAddress, bssPort);
            primaryStage.setScene(getPrimaryScene());
            this.node.start();
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

    @Override
    public void stop() throws Exception {
        this.node.stop();
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
