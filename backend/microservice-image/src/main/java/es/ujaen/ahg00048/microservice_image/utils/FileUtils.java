package es.ujaen.ahg00048.microservice_image.utils;

import java.nio.file.Paths;
import java.util.Locale;

public class FileUtils {
    public static String parseFileName(String fileName) {
        return Paths.get(fileName).getFileName().toString();
    }

    public static boolean hasImageExtension(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).matches(".*\\.(jpg|jpeg|png)$");
    }
}
