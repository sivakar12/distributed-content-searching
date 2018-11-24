package snutella.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import snutella.Node;
import snutella.logging.LogMessage;
import snutella.logging.LogMessageListener;
import snutella.logging.LogsManager;
import snutella.neighbors.Neighbor;
import snutella.neighbors.NeighborListListener;
import snutella.queryresults.QueryResultItem;
import snutella.queryresults.QueryResultsListener;
import snutella.queryresults.QueryResultsManager;

import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable, NeighborListListener,
        LogMessageListener, QueryResultsListener {
    Node node;
    @FXML
    public Label title;
    @FXML
    public Label neighborDetails;
    @FXML
    public Label filesList;
    @FXML
    private Label logs;
    @FXML
    private TextField queryText;
    @FXML
    private VBox queryResults;

    public MainController(Node node) {
        this.node = node;
        this.node.getNeighborManager().addListener(this);
        LogsManager.getInstance().addListener(this);
        QueryResultsManager.getInstance().addListener(this);

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

    @Override
    public void onNewLogMessage(LogMessage message) {
        String newText = message.toString() + "\n" + this.logs.getText();
        Platform.runLater(() -> {
            this .logs.setText(newText);
        });
    }
    @FXML
    public void clearLogs() {
        this.logs.setText("");
    }

    @FXML
    public void runQuery() {
        this.node.sendQuery(this.queryText.getText());
    }

    @Override
    public void resultsChanged(List<QueryResultItem> items) {
        Platform.runLater(() -> {
            this.queryResults.getChildren().removeAll();
            items.stream()
                    .map(i -> {
                        Label label = new Label(i.toString());
                        Button button = new Button("GET");
                        button.setOnAction(e -> {
                            System.out.println("Downloading " + i.getFilename());
                        });
//                        label.setMaxWidth(Double.MAX_VALUE);

                        HBox.setHgrow(label, Priority.ALWAYS);
                        HBox hbox = new HBox(label, button);
                        hbox.setMaxWidth(Double.MAX_VALUE);
                        return hbox;
                    }).forEach(box -> {
                        this.queryResults.getChildren().add(box);
            });
        });
    }
}
