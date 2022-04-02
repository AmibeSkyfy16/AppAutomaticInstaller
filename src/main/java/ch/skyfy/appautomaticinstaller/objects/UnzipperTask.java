package ch.skyfy.appautomaticinstaller.objects;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

public class UnzipperTask extends Task<Integer>{

    private final String destinationFolderPath;;

//    private final ZipFile zipFile;

    private final SimpleObjectProperty<ZipFile> zipFileProperty;


    public UnzipperTask(String destinationFolderPath, SimpleObjectProperty<ZipFile> zipFileProperty){
        this.destinationFolderPath = destinationFolderPath;
        this.zipFileProperty = zipFileProperty;

        updateValue(0);
        updateProgress(0, 100);
    }

    @Override
    protected Integer call(){

        final ProgressMonitor progressMonitor = zipFileProperty.get().getProgressMonitor();

        try{
            zipFileProperty.get().extractAll(destinationFolderPath);
        }catch(ZipException e){
            e.printStackTrace();
        }
        System.out.println("dsdsdsd");

        while(zipFileProperty.get().getProgressMonitor().getState() == ProgressMonitor.State.BUSY){
            updateProgress(zipFileProperty.get().getProgressMonitor().getPercentDone(), 100);
            updateValue(zipFileProperty.get().getProgressMonitor().getPercentDone());
            try{
                Thread.sleep(200);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        return 0;
    }

}
