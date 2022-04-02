package ch.skyfy.appautomaticinstaller.util;

import ch.skyfy.appautomaticinstaller.models.Program;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtils{

    public static List<String> getRootFiles(String zipFilePath) throws FileNotFoundException{
        try{
            if(!Files.exists(Path.of(zipFilePath))){
                throw new FileNotFoundException("FICHIER PAS TROUVE");
            }
            return new ZipFile(zipFilePath).getFileHeaders().stream()
                    .map(fileHeader -> fileHeader.getFileName().split("/")[0])
                    .distinct()
                    .collect(Collectors.toList());
        }catch(ZipException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static boolean matchesRootFiles(List<String> rootFiles, String destinationFolderPath){

        final File destinationFolder = new File(destinationFolderPath);

        if(destinationFolder == null || !destinationFolder.exists()){
            return false;
        }

        for(String str : rootFiles){
            if(!new File(destinationFolderPath + "/" + str).exists()) return false;
        }

        return true;
    }

    public static MyEntry<List<String>, Program.Status> isInstalled(String zipFilePath, String destinationFolderPath) throws FileNotFoundException{
        final List<String> rootFiles = getRootFiles(zipFilePath);
        for(String str : rootFiles){
            if(!Files.exists(Path.of(destinationFolderPath + "/" + str))) return new MyEntry(rootFiles, Program.Status.NOT_INSTALLED);
        }
        return new MyEntry(rootFiles, Program.Status.INSTALLED);
    }

    public static class MyEntry<K, V> implements Map.Entry<K, V>{
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

}
