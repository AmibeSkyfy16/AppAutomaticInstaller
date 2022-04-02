package ch.skyfy.appautomaticinstaller.controllers;

import ch.skyfy.appautomaticinstaller.AppAutomaticInstaller;
import ch.skyfy.appautomaticinstaller.models.Program;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ProgramController implements Initializable, Consumer<Boolean>{

    @FXML
    private VBox root_Vbox;

//    @FXML
//    private VBox container_VBox;

    @FXML
    private CheckBox appName_CheckBox;

    @FXML
    private ImageView zipFileStatus_ImageView, destinationFolderStatus_ImageView;

    @FXML
    private ImageView delete_ImageView;

    @FXML
    private Label status_Label;

    @FXML
    private TextField zipFile_TextField, destinationFolder_TextField;

    @FXML
    private Button zipFile_Button, destinationFolder_Button;

    @FXML
    private GridPane status_GridPane;

    @FXML
    private ProgressBar status_ProgressBar;

    private final Program program;

    private final Consumer<Node> deleteChild;

    public ProgramController(Program program, Consumer<Node> deleteChild){
        this.program = program;
        this.deleteChild = deleteChild;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        appName_CheckBox.setSelected(program.isSelected());
        appName_CheckBox.setText(program.getProgramName());
        zipFile_TextField.setText(program.getZipFilePath());
        destinationFolder_TextField.setText(program.getDestinationFolderPath());

        initDeleteImageView();

        createBinding();

        accept(false);

    }

    private void initDeleteImageView(){

        final Image delete = new Image(AppAutomaticInstaller.class.getResource("images/delete.png").toExternalForm());
        final Image deleteHover = new Image(AppAutomaticInstaller.class.getResource("images/delete-hover.png").toExternalForm());

        delete_ImageView.setOnMouseClicked(event -> {
            deleteChild.accept(root_Vbox);
        });

        delete_ImageView.setOnMouseEntered(event -> delete_ImageView.setImage(deleteHover));
        delete_ImageView.setOnMouseExited(event -> delete_ImageView.setImage(delete));
    }

    private void createBinding(){

        program.selectedProperty().bind(appName_CheckBox.selectedProperty());
        program.programNameProperty().bind(appName_CheckBox.textProperty());
        program.zipFilePathProperty().bind(zipFile_TextField.textProperty());
        program.destinationFolderPathProperty().bind(destinationFolder_TextField.textProperty());

        program.getUnzipperService().stateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == Worker.State.RUNNING){
                status_ProgressBar.progressProperty().bind(program.getUnzipperService().progressProperty());
                status_Label.textProperty().bind(program.getUnzipperService().valueProperty().asString("%d %%"));
            }else if(newValue == Worker.State.SUCCEEDED){
                status_ProgressBar.progressProperty().unbind();
                status_ProgressBar.progressProperty().setValue(1);
                status_Label.textProperty().unbind();
                status_Label.textProperty().set("100 %");
            }else{
                status_ProgressBar.progressProperty().unbind();
                status_ProgressBar.progressProperty().setValue(0);
                status_Label.textProperty().unbind();
                status_Label.textProperty().set("0 %");
            }
        });

        final Image appInstalled = new Image(AppAutomaticInstaller.class.getResource("images/appInstalled.png").toExternalForm());
        final Image appNotInstalled = new Image(AppAutomaticInstaller.class.getResource("images/appNotInstalled.png").toExternalForm());
        final Image fileExist = new Image(AppAutomaticInstaller.class.getResource("images/fileExist.png").toExternalForm());
        final Image fileNotExist = new Image(AppAutomaticInstaller.class.getResource("images/fileNotExist.png").toExternalForm());


        zipFileStatus_ImageView.imageProperty().bind(Bindings.when(program.zipFileStatusProperty()
                .isEqualTo(Program.Status.INSTALLED))
                .then(appInstalled)
                .otherwise(Bindings.when(program.zipFileStatusProperty()
                        .isEqualTo(Program.Status.NOT_INSTALLED))
                        .then(appNotInstalled)
                        .otherwise(Bindings.when(program.zipFileStatusProperty()
                            .isEqualTo(Program.Status.NOT_EXIST))
                                .then(fileNotExist)
                                .otherwise(fileNotExist)
                        )
                )
        );

        destinationFolderStatus_ImageView.imageProperty().bind(Bindings.when(program.destinationFolderStatusProperty()
                .isEqualTo(Program.Status.EXIST))
                .then(fileExist)
                .otherwise(fileNotExist));

        zipFile_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            program.zipFileStatusProperty().set(program.updateZipFileStatus());
        });

        destinationFolder_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            program.destinationFolderStatusProperty().set(program.updateDestinationFolderStatus());
        });

    }

    @Override
    public synchronized void accept(Boolean value){
        if(value) root_Vbox.getChildren().add(status_GridPane);
        else root_Vbox.getChildren().remove(status_GridPane);
        root_Vbox.setMinHeight((value) ? 160 : 130);
        root_Vbox.setPrefHeight((value) ? 160 : 130);
        root_Vbox.setMaxHeight((value) ? 160 : 130);
    }

}
