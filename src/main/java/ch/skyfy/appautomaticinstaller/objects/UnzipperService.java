package ch.skyfy.appautomaticinstaller.objects;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lingala.zip4j.ZipFile;


public class UnzipperService extends Service<Integer>{

    private final String destinationFolderPath;

    private final SimpleObjectProperty<ZipFile> zipFileProperty;


    public UnzipperService(String destinationFolderPath, SimpleObjectProperty<ZipFile> zipFileProperty){
        this.zipFileProperty = zipFileProperty;
        this.destinationFolderPath = destinationFolderPath;
    }

    @Override
    protected Task<Integer> createTask(){
        return new UnzipperTask(destinationFolderPath, zipFileProperty);
    }
}
