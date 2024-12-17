package mg.framework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Folder {

    public static void create(String path, String folder_name) throws IOException{
        Path newPath = Paths.get(path, folder_name);
        try {
            Files.createDirectories(newPath);
        } catch (RuntimeException e) {
            throw new IOException();
        }
    }

    public static void delete(String folder_path, String folder_name) throws Exception{
        Path folder = Paths.get(folder_path +"/"+ folder_name);
        if (Files.exists(folder)) {
            Files.walk(folder)
                .sorted((p1, p2) -> p2.compareTo(p1))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (Exception e) {
                        System.err.println("Error during deleting " + p + " : " + e.getMessage());
                    }
                });
        }
    }
}