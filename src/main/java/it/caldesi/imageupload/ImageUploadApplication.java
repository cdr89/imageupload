package it.caldesi.imageupload;

import it.caldesi.imageupload.service.ImageStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootApplication
public class ImageUploadApplication {

    @Autowired
    private ImageStoreService imageStoreService;

    public static void main(String[] args) {
        SpringApplication.run(ImageUploadApplication.class, args);
    }

    @PreDestroy
    public void tearDown() throws InterruptedException {
        System.out.println("Shutting Down...");
        ThreadPoolExecutor executor = imageStoreService.getExecutor();
        while (executor.getTaskCount() != executor.getCompletedTaskCount()) {
            System.err.println("count= " + executor.getTaskCount() + "," + executor.getCompletedTaskCount());
            Thread.sleep(5000);
        }
        executor.shutdown();
        executor.awaitTermination(60, SECONDS);
    }
}
