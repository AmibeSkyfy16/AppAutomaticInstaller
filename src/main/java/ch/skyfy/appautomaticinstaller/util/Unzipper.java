package ch.skyfy.appautomaticinstaller.util;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper extends Task<Double>{

    private final File zipFilePath, destinationFolderPath;

    private double currentPercent;

    public Unzipper(String zipFilePath, String destinationFolderPath, DoubleProperty progressProperty, StringProperty textProperty){
        this.zipFilePath = new File(zipFilePath);
        this.destinationFolderPath = new File(destinationFolderPath);

        updateValue(0d);

        final DoubleBinding currentPercentDoubleBinding = Bindings.createDoubleBinding(() -> valueProperty().get(), valueProperty());
        progressProperty.bind(currentPercentDoubleBinding.divide(100));
        textProperty.bind(Bindings.format("%.1f %%", currentPercentDoubleBinding));

    }

    public void unzip() throws IOException{

        final FileInputStream fis = new FileInputStream(zipFilePath);
        final FileChannel fc = fis.getChannel();


        ZipEntry ze;

        try(final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))){
//            final long length = zipFilePath.length();
            while((ze = zis.getNextEntry()) != null){
                File f = new File(destinationFolderPath.getCanonicalPath(), ze.getName());
                if(ze.isDirectory()){
                    f.mkdirs();
                    continue;
                }
                f.getParentFile().mkdirs();
                try(final OutputStream fos = new BufferedOutputStream(new FileOutputStream(f))){
                    final byte[] buf = new byte[8192];
                    int bytesRead;
                    final long length = zipFilePath.length();
                    while((bytesRead = zis.read(buf)) != -1){
                        fos.write(buf, 0, bytesRead);
                        System.out.println(length);
                        System.out.println(fc.position());
                        currentPercent = ((double) fc.position() / length) * 100;
                        System.out.println(currentPercent);
                        updateValue(currentPercent);
                    }
                }catch(final IOException e){
                    f.delete();
                    throw e;
                }
            }
        }finally{

            fis.close();
        }
        updateValue(100d);
    }

    @Override
    protected void updateValue(Double value){
        super.updateValue(value);
    }

    @Override
    protected Double call() throws Exception{


            try{
                unzip();
            }catch(IOException e){
                e.printStackTrace();
            }

        return currentPercent;
    }

}
