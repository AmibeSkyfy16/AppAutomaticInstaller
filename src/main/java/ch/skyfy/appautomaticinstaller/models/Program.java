package ch.skyfy.appautomaticinstaller.models;

import ch.skyfy.appautomaticinstaller.objects.UnzipperService;
import ch.skyfy.appautomaticinstaller.util.FileUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import net.lingala.zip4j.ZipFile;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Program{

    private final SimpleBooleanProperty selected;

//    private final SimpleBooleanProperty installed;

    private final SimpleBooleanProperty installing;

    private final SimpleBooleanProperty exist;

    private final SimpleStringProperty programName;

    private final SimpleStringProperty zipFilePath, destinationFolderPath;

    private final SimpleObjectProperty<ZipFile> zipFile;

    private final UnzipperService unzipperService;

    private List<String> rootFiles;

    private final SimpleObjectProperty<Status> zipFileStatus;
    private final SimpleObjectProperty<Status> destinationFolderStatus;

    public enum Status{
        INSTALLED,
        NOT_INSTALLED,
        EXIST,
        NOT_EXIST;
    }

    public Program(boolean selected, String programName, String zipFilePath, String destinationFolderPath){
        this.selected = new SimpleBooleanProperty(selected);
        this.programName = new SimpleStringProperty(programName);
        this.zipFilePath = new SimpleStringProperty(zipFilePath);
        this.destinationFolderPath = new SimpleStringProperty(destinationFolderPath);

        zipFile = new SimpleObjectProperty(null);
//        installed = new SimpleBooleanProperty(updateRootFiles());
        installing = new SimpleBooleanProperty(false);
        exist = new SimpleBooleanProperty(true);

        zipFileStatus = new SimpleObjectProperty(updateZipFileStatus());
        destinationFolderStatus = new SimpleObjectProperty(updateDestinationFolderStatus());

        unzipperService = new UnzipperService(destinationFolderPath, zipFile);

        setZipFile();

    }

    private Status updateRootFiles(){
        try{
            final FileUtils.MyEntry<List<String>, Status> myEntry = FileUtils.isInstalled(zipFilePath.get(), destinationFolderPath.get());
            rootFiles = myEntry.getKey();
            return myEntry.getValue();
        }catch(FileNotFoundException e){
//            e.printStackTrace();
            return Status.NOT_EXIST;
        }
    }

    public Status updateDestinationFolderStatus(){
        if(!Files.exists(Path.of(destinationFolderPath.get())))return Status.NOT_EXIST;
        else return Status.EXIST;
    }

    public Status updateZipFileStatus(){
        return updateRootFiles();
    }

    // --------------------- GETTERS --------------------- \\

    public boolean isSelected(){
        return selected.get();
    }

    public SimpleBooleanProperty selectedProperty(){
        return selected;
    }

//    public boolean isInstalled(){
//        return installed.get();
//    }
//
//    public SimpleBooleanProperty installedProperty(){
//        return installed;
//    }

    public String getProgramName(){
        return programName.get();
    }

    public SimpleStringProperty programNameProperty(){
        return programName;
    }

    public String getZipFilePath(){
        return zipFilePath.get();
    }

    public SimpleStringProperty zipFilePathProperty(){
        return zipFilePath;
    }

    public String getDestinationFolderPath(){
        return destinationFolderPath.get();
    }

    public SimpleStringProperty destinationFolderPathProperty(){
        return destinationFolderPath;
    }

    public UnzipperService getUnzipperService(){
        return unzipperService;
    }


    public List<String> getRootFiles(){
        return rootFiles;
    }

    public SimpleBooleanProperty installingProperty(){
        return installing;
    }

    public boolean isInstalling(){
        return installing.get();
    }


    // --------------------- SETTERS --------------------- \\

    public void setZipFile(){
        zipFile.set(new ZipFile(zipFilePath.get()){{setRunInThread(true);}});
    }

    public void setInstalling(boolean installing){
        this.installing.set(installing);
    }

//    public void setInstalled(boolean installed){
//        this.installed.set(installed);
//    }


    public Status getZipFileStatus(){
        return zipFileStatus.get();
    }

    public SimpleObjectProperty<Status> zipFileStatusProperty(){
        return zipFileStatus;
    }

    public void setZipFileStatus(Status zipFileStatus){
        this.zipFileStatus.set(zipFileStatus);
    }

    public ZipFile getZipFile(){
        return zipFile.get();
    }

    public SimpleObjectProperty<ZipFile> zipFileProperty(){
        return zipFile;
    }

    public Status getDestinationFolderStatus(){
        return destinationFolderStatus.get();
    }

    public SimpleObjectProperty<Status> destinationFolderStatusProperty(){
        return destinationFolderStatus;
    }

    public void setDestinationFolderStatus(Status destinationFolderStatus){
        this.destinationFolderStatus.set(destinationFolderStatus);
    }
}
