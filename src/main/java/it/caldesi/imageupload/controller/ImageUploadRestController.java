package it.caldesi.imageupload.controller;

import it.caldesi.imageupload.model.UploadFileResponse;
import it.caldesi.imageupload.service.ImageStoreService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Log
public class ImageUploadRestController {

    @Autowired
    private ImageStoreService imageStoreService;

    @PostMapping (value = "/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam ("file") MultipartFile multipartFile) throws IOException {
        imageStoreService.validate(multipartFile);
        String imageUUID = imageStoreService.storeImage(multipartFile);
        UploadFileResponse uploadFileResponse = new UploadFileResponse(imageUUID);

        return ok(uploadFileResponse);
    }

}
