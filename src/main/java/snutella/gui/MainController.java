package snutella.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import snutella.Node;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    Node node;
    @FXML
    public Label title;

    public MainController(Node node) {
        this.node = node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String nodeDetails = this.node.getAddress().toString().substring(1) + ":" + this.node.getPort();
        this.title.setText(nodeDetails);
    }
}
