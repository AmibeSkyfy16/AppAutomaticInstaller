package ch.skyfy.appautomaticinstaller.models;

import ch.skyfy.appautomaticinstaller.stages.AddProgrammeStage;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AppAutomaticInstallerController implements Initializable{

    @FXML
    private VBox programList_VBox;

    @FXML
    private Button addApp_Button;

    @FXML
    private Button install_Button;

    private final Consumer<Node> deleteChild;

    private final Stage primaryStage;

    private final List<Program> programs;

    private ExecutorService installApplicationExecutor;

    public AppAutomaticInstallerController(Stage primaryStage){
        this.primaryStage = primaryStage;
        programs = new ArrayList<>();

        deleteChild = node -> {
            programList_VBox.getChildren().remove(node);
        };

        primaryStage.setOnCloseRequest(this::onCloseRequest);
    }

    private void onCloseRequest(Event event){

        programs.forEach(program -> {
            program.zipFileProperty().get().getProgressMonitor().setCancelAllTasks(true);
        });

        new Thread(() -> {
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            programs.forEach(program -> {

                if(!program.isInstalling()) return;

                final String destinationFolderPath = program.getDestinationFolderPath();

                program.getRootFiles().forEach(s -> {
                    try{
                        FileUtils.forceDelete(new File(Path.of(destinationFolderPath + "/" + s).toUri()));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                });
                final File destinationFolder = new File(destinationFolderPath);
                if(destinationFolder.list().length == 0){
                    try{
                        FileUtils.forceDelete(destinationFolder);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }){{
            setDaemon(false);
        }}.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        initAddProgramButton();
        initInstallButton();
    }

    private void initAddProgramButton(){
        addApp_Button.setOnAction(event -> new AddProgrammeStage(primaryStage, this).show());
    }

    private void initInstallButton(){
        install_Button.setOnAction(new EventHandler<>(){
            private boolean atLeastOneInstalling = false;

            @Override
            public void handle(ActionEvent event){
                atLeastOneInstalling = false;
                installApplicationExecutor = Executors.newCachedThreadPool(r -> new Thread(r){{
                    setDaemon(true);
                }});
                programs.forEach(program -> {
                    if(program.isSelected()){
                        final File destinationFolder = new File(program.getDestinationFolderPath());
                        if(!destinationFolder.exists()){
                            destinationFolder.mkdir();
                        }
                        atLeastOneInstalling = true;
                        program.setInstalling(true);
                        program.setZipFile();
                        program.getUnzipperService().setExecutor(installApplicationExecutor);
                        program.getUnzipperService().start();
                    }
                });
                installApplicationExecutor.shutdown();
                if(atLeastOneInstalling) install_Button.setDisable(true);
            }
        });

    }

    public Consumer<Node> getDeleteChild(){
        return deleteChild;
    }

    public List<Program> getPrograms(){
        return programs;
    }

    public VBox getProgramList_VBox(){
        return programList_VBox;
    }

    public Button getInstall_Button(){
        return install_Button;
    }
}
