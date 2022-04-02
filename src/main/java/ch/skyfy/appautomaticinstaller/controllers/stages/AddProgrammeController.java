package ch.skyfy.appautomaticinstaller.controllers.stages;

import ch.skyfy.appautomaticinstaller.AppAutomaticInstaller;
import ch.skyfy.appautomaticinstaller.controllers.ProgramController;
import ch.skyfy.appautomaticinstaller.models.AppAutomaticInstallerController;
import ch.skyfy.appautomaticinstaller.models.Program;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AddProgrammeController implements Initializable{

    @FXML
    private GridPane importFile_GridPane, createManually_GridPane;

    @FXML
    private TextField importedFile_TextField;

    @FXML
    private TextField programName_TextField, zipFilePath_TextField, destinationFolderPath_TextField;

    @FXML
    private Button importedFile_Button;

    @FXML
    private Button cancel_Button, ok_Button;

    @FXML
    private Button zipFilePath_Button, destinationFolderPath_Button;

    @FXML
    private CheckBox importFile_CheckBox, createManually_CheckBox;

    private final Stage addProgrammeStage;

    private final AppAutomaticInstallerController context;


    public AddProgrammeController(Stage addProgrammeStage, AppAutomaticInstallerController context){
        this.addProgrammeStage = addProgrammeStage;
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        initCheckBox();
        initFileChooserButtons();
        initCancelButton();
        initAddButton();

    }

    private void initCheckBox(){
        importFile_CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                createManually_CheckBox.setSelected(false);
                createManually_GridPane.setDisable(true);
                importFile_GridPane.setDisable(false);
            }
        });

        createManually_CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                importFile_CheckBox.setSelected(false);
                importFile_GridPane.setDisable(true);
                createManually_GridPane.setDisable(false);
            }
        });

    }

    private void initFileChooserButtons(){
        importedFile_Button.setOnAction(this::openFileChooser);
        zipFilePath_Button.setOnAction(this::openFileChooser);
        destinationFolderPath_Button.setOnAction(this::openDirectoryChooser);
    }

    private void initCancelButton(){
        cancel_Button.setOnAction(event -> addProgrammeStage.close());
    }

    private void initAddButton(){
        ok_Button.setOnAction(event -> {
            if(importFile_CheckBox.isSelected()){

                final AtomicBoolean selected = new AtomicBoolean();
                final AtomicReference<String> programName = new AtomicReference<>("");
                final AtomicReference<String> path = new AtomicReference<>("");
                final AtomicReference<String> destinationPath = new AtomicReference<>("");

                final File file = new File(importedFile_TextField.getText());

                if(file == null || !file.exists()){
                    return;
                }

                // TODO implement with GSON
//                try(final FileReader fileReader = new FileReader(file)){
//                    final JSONParser jsonParser = new JSONParser();
//                    final Object obj = jsonParser.parse(fileReader);
//                    final JSONArray employeeList = (JSONArray) obj;
//                    employeeList.forEach(o -> {
//                        final JSONObject jsonObject = (JSONObject) o;
//                        jsonObject.forEach((o1, o2) -> {
//                            final JSONObject values = (JSONObject) o2;
//                            selected.set(Boolean.parseBoolean((String) values.get("selected")));
//                            programName.set(o1.toString());
//                            path.set((String) values.get("path"));
//                            destinationPath.set((String) values.get("destinationPath"));
//                            addProgram(selected.get(), programName.get(), path.get(), destinationPath.get());
//                        });
//                    });
//                }catch(IOException | ParseException e){
//                    e.printStackTrace();
//                }
            }else if(createManually_CheckBox.isSelected()){
                addProgram(true, programName_TextField.getText(), zipFilePath_TextField.getText(), destinationFolderPath_TextField.getText());
            }
            addProgrammeStage.close();
        });
    }

    private void addProgram(boolean selected, String programName, String zipFilePath, String destinationFolderPath){

        final FXMLLoader fxmlLoader = new FXMLLoader(AppAutomaticInstaller.class.getResource("fxml/Programme3.fxml"));
        final Program program = new Program(selected, programName, zipFilePath, destinationFolderPath);
        final ProgramController programController = new ProgramController(program, context.getDeleteChild());
        fxmlLoader.setController(programController);

        Parent root = null;
        try{
            root = fxmlLoader.load();
        }catch(IOException e){
            e.printStackTrace();
        }

        program.getUnzipperService().addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            program.setInstalling(false);
            program.zipFileStatusProperty().set(Program.Status.INSTALLED);

            Executors.newSingleThreadScheduledExecutor(r -> new Thread(r){
                {
                    setDaemon(true);
                }
            }).schedule(() -> {
                Platform.runLater(() -> {
                    if(((ThreadPoolExecutor) program.getUnzipperService().getExecutor()).isTerminated()){
                        context.getInstall_Button().setDisable(false);
                    }
                });
            }, 1500, TimeUnit.MILLISECONDS);
        });

        program.installingProperty().addListener((observable, oldValue, newValue) -> {
            programController.accept(newValue);
        });

        context.getPrograms().add(program);
        context.getProgramList_VBox().getChildren().add(root);
    }

    private void openFileChooser(ActionEvent actionEvent){
        final FileChooser fileChooser = new FileChooser();
        final FileChooser.ExtensionFilter extFilter =
                (actionEvent.getSource() == importedFile_Button) ?
                        new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json") :
                        new FileChooser.ExtensionFilter("ZIP Files (*.zip)", "*.zip");

        fileChooser.getExtensionFilters().add(extFilter);
        final File file = fileChooser.showOpenDialog(addProgrammeStage);
        if(file != null){
            if(actionEvent.getSource() == zipFilePath_Button){
                zipFilePath_TextField.setText(file.getAbsolutePath());
            }else if(actionEvent.getSource() == destinationFolderPath_Button){
                destinationFolderPath_TextField.setText(file.getAbsolutePath());
            }
        }
    }

    private void openDirectoryChooser(ActionEvent actionEvent){
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File file = directoryChooser.showDialog(addProgrammeStage);
        if(file != null){
            if(actionEvent.getSource() == zipFilePath_Button){
                zipFilePath_Button.setText(file.getAbsolutePath());
            }else if(actionEvent.getSource() == destinationFolderPath_Button){
                destinationFolderPath_TextField.setText(file.getAbsolutePath());
            }
        }
    }

}
