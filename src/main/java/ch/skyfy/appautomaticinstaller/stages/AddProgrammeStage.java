package ch.skyfy.appautomaticinstaller.stages;

import ch.skyfy.appautomaticinstaller.AppAutomaticInstaller;
import ch.skyfy.appautomaticinstaller.models.AppAutomaticInstallerController;
import ch.skyfy.appautomaticinstaller.controllers.stages.AddProgrammeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AddProgrammeStage extends Stage{

    public AddProgrammeStage(Stage primaryStage, AppAutomaticInstallerController context){
        super(StageStyle.DECORATED);
        try{
            final FXMLLoader fxmlLoader = new FXMLLoader(AppAutomaticInstaller.class.getResource("fxml/stages/AddProgram.fxml"));
            final AddProgrammeController addProgrammeController = new AddProgrammeController(this, context);
            fxmlLoader.setController(addProgrammeController);
            final Parent root = fxmlLoader.load();

            this.setScene(new Scene(root));
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(primaryStage);
            this.setMinHeight(400);
            this.setMinWidth(400);
            this.setX((primaryStage.widthProperty().getValue() - 300) / 2 + (primaryStage.getX() / 2));
            this.setY((primaryStage.heightProperty().getValue() - 200) / 2 + (primaryStage.getY() / 2));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
