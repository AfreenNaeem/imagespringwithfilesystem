package com.example.imageSpring.images.controllerFileSys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.imageSpring.images.seviceFileSys.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    // Upload to DB
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = imageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

    // View from DB
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws IOException {
        byte[] image = imageService.downloadImage(fileName);
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType("image/jpeg"))
                .body(image);
    }

    // Upload to File System
    @PostMapping("/filesys")
    public ResponseEntity<?> uploadImageToFileSystem(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = imageService.uploadImageToFileSystem(file);
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

    @GetMapping("/filesys/{fileName}")
    public ResponseEntity<byte[]> getImageFromFileSystem(@PathVariable String fileName) throws IOException {
        // Sanitize fileName to prevent path traversal attacks
        String safeFileName = Paths.get(fileName).getFileName().toString();
        
     
        File file = new File("C:\\Users\\121\\Downloads\\FileStorage", safeFileName);

        // Check if file exists
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        // Read bytes from the file
        byte[] image = Files.readAllBytes(file.toPath());

        // Detect content type
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Fallback
        }

        // Return the file as a ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }



    @GetMapping("/filesys/download/{fileName}")
    public ResponseEntity<Resource> downloadImageFile(@PathVariable String fileName) {
        try {
            Resource file = imageService.downloadImageFile(fileName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);
        } catch (Exception e) {
            e.printStackTrace();  // 🔥 this will print the real cause in console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/send")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String subject,
                            @RequestParam String body) {
        try {
            imageService.sendSimpleMail(to, subject, body);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}



