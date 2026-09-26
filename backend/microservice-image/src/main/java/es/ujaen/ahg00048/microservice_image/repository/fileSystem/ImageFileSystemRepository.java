package es.ujaen.ahg00048.microservice_image.repository.fileSystem;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Repository
public class ImageFileSystemRepository {
    @Getter
    @Setter
    private String rootPath;


    public ImageFileSystemRepository(@Value("${app.persistence.filesystem.root}") String rootPath) {
        this.rootPath = rootPath;

        Path path = Paths.get(rootPath);
        path = path.toAbsolutePath();

        assert Files.exists(path) && Files.isDirectory(path);
    }


    public byte[] findByPath(String path) throws IOException {
        File file = new File(rootPath + "/" + path);

        try (FileInputStream iStream = new FileInputStream(file)) {
            return iStream.readAllBytes();
        }
    }

    public void save(String path, byte[] imageData) throws IOException {
        File file = new File(rootPath + "/" + path);

        try (FileOutputStream oStream = new FileOutputStream(file)) {
            oStream.write(imageData);
        }
    }

    public void delete(String path) throws IOException {
        File file = new File(rootPath + "/" + path);

        Files.delete(file.toPath());
    }
}
