package it.caldesi.imageupload.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.imageio.ImageIO.read;

@Service
public class ImageScalingService {

    @Value ("${app.images.max-width-size}")
    private int maxWidthSize;

    public BufferedImage downscaleImage(@NonNull final MultipartFile multipartFile) throws IOException {
        BufferedImage originalImage = read(multipartFile.getInputStream());
        if (needsToBeDownscaled(originalImage)) {
            int scaledImageHeight = calculateScaledImageHeight(originalImage);
            BufferedImage scaledImage = new BufferedImage(maxWidthSize, scaledImageHeight, TYPE_INT_RGB);
            Graphics2D g = scaledImage.createGraphics();
            g.drawImage(originalImage, 0, 0, maxWidthSize, scaledImageHeight, null);
            g.dispose();

            return scaledImage;
        } else {
            return originalImage;
        }
    }

    private int calculateScaledImageHeight(final BufferedImage originalImage) {
        return (int) ((double) (originalImage.getHeight() * maxWidthSize) / originalImage.getWidth());
    }

    private boolean needsToBeDownscaled(final BufferedImage originalImage) {
        return originalImage.getWidth() > maxWidthSize;
    }

}
