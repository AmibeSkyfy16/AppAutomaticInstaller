module ch.skyfy.appautomaticinstaller {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires zip4j;
//    requires json.simple;
    requires org.apache.commons.io;

    exports ch.skyfy.appautomaticinstaller to
            javafx.graphics;

    opens ch.skyfy.appautomaticinstaller.models to
            javafx.fxml;

    opens ch.skyfy.appautomaticinstaller.controllers.stages to
            javafx.fxml;

    opens  ch.skyfy.appautomaticinstaller.controllers to
            javafx.fxml;

}