package it.caldesi.imageupload.service;

import it.caldesi.imageupload.exception.BadImageFormatException;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.logging.Level.SEVERE;
import static javax.imageio.ImageIO.write;

@Service
@Log
public class ImageStoreService {

    @Value ("${app.images.accepted-format-types}")
    List<String> acceptedFormatTypes;

    @Value ("${app.upload.base-dir}")
    String uploadDir;

    @Value ("${app.images.output-format}")
    final String imageOutputFormat = "jpg";

    private int resizePoolSize;

    @Autowired
    private ImageScalingService imageScalingService;

    private ThreadPoolExecutor executor;

    public String storeImage(@NonNull final MultipartFile multipartFile) throws IOException {
        BufferedImage scaledImage = imageScalingService.downscaleImage(multipartFile);

        UUID uuid = randomUUID();
        File outputFile = getImageFile(uuid);
        write(scaledImage, imageOutputFormat, outputFile);
        log.info("Image stored in: " + outputFile.getAbsolutePath());

        return uuid.toString();
    }

    public void validate(@NonNull final MultipartFile multipartFile) {
        if (!acceptedFormatTypes.contains(multipartFile.getContentType())) {
            throw new BadImageFormatException();
        }
    }

    private File getImageFile(@NonNull UUID uuid) {
        String shardFolderPath = getShardFolderPath(uuid);
        String imageFileName = getImageFileName(uuid);

        return Paths.get(shardFolderPath, imageFileName).toFile();
    }

    private String getImageFileName(@NonNull UUID uuid) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(uuid.toString());
        stringBuilder.append('.');
        stringBuilder.append(imageOutputFormat);

        return stringBuilder.toString();
    }

    private String getShardFolderPath(@NonNull UUID uuid) {
        String shardFolder = uuid.toString().substring(0, 2);
        Path shardFolderPath = Paths.get(uploadDir, shardFolder);
        createShardFolderIfNotExists(shardFolderPath);

        return shardFolderPath.toAbsolutePath().toString();
    }

    private void createShardFolderIfNotExists(final Path shardFolderPath) {
        if (!exists(shardFolderPath)) {
            try {
                createDirectories(shardFolderPath);
            } catch (IOException e) {
                log.log(SEVERE, "Cannot create shard folder: " + shardFolderPath.toString(), e);
            }
        }
    }

    @Value ("${app.images.resize-pool-size}")
    public void setResizePoolSize(final int resizePoolSize) {
        this.resizePoolSize = resizePoolSize;
        executor = (ThreadPoolExecutor) newFixedThreadPool(resizePoolSize);
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
