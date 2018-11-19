package snutella.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import snutella.Node;
import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListListener;

import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable, NeighborListListener {
    Node node;
    @FXML
    public Label title;
    @FXML
    public Label neighborDetails;
    @FXML
    public Label filesList;
    @FXML
    private Button refreshFilesButton;

    public MainController(Node node) {
        this.node = node;
        this.node.getNeighborManager().addListener(this);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String nodeDetails = this.node.getAddress().toString().substring(1) + ":" + this.node.getPort();
        this.title.setText(nodeDetails);
        String fileDetails = this.node.getFileManager().getFilesString();
        this.filesList.setText(fileDetails);
    }

    @Override
    public void onUpdate(List<Neighbor> neighbors) {
        String detailsString = neighbors.stream().map(Neighbor::getStatusString).reduce("", (s1, s2) -> s1 + "\n" + s2);
        Platform.runLater(() -> {
            this.neighborDetails.setText(detailsString);
        });
    }

    @FXML
    public void refreshFiles() {
        this.node.getFileManager().refreshFiles();
        this.filesList.setText(this.node.getFileManager().getFilesString());
    }

}
