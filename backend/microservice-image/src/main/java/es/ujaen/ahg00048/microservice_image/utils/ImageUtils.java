package es.ujaen.ahg00048.microservice_image.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;

public class ImageUtils {
    public static String parseFileName(String fileName) {
        return Paths.get(fileName).getFileName().toString();
    }

    public static boolean hasImageExtension(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).matches(".*\\.(jpg|jpeg|png)$");
    }

    public static byte[] optimizeImage(MultipartFile file)
            throws IllegalStateException, RuntimeException {
        try (
                InputStream inputStream = file.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            String fileName = file.getOriginalFilename();

            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(fileName.substring(fileName.lastIndexOf('.') + 1));

            if (!imageWriters.hasNext())
                throw new IllegalStateException();

            ImageWriter imageWriter = imageWriters.next();

            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
                 float imageQuality = 0.3f;

                 imageWriter.setOutput(imageOutputStream);

                ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

                // Set the compress quality metrics
                imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imageWriteParam.setCompressionQuality(imageQuality);

                // Compress and insert the image into the byte array.
                imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

                byte[] imageBytes = outputStream.toByteArray();

                // close all streams
                return imageBytes;
            } finally {
                imageWriter.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
