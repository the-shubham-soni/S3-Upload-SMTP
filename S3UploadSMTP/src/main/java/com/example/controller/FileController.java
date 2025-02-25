package com.example.controller;

import com.example.service.EmailService;
import com.example.service.S3Service;

import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@Validated
public class FileController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailService emailService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file")  MultipartFile file,
            @RequestParam("email") String email) throws IOException {

        // Save file locally
        File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(localFile);

        // Upload to S3
        s3Service.uploadFile(localFile);

        // Send email
        emailService.sendEmail(email, "File Upload Successful", "Your file has been successfully uploaded.");

        // Delete local file
        localFile.delete();

        return ResponseEntity.ok("File uploaded and email sent successfully.");
    }
}
