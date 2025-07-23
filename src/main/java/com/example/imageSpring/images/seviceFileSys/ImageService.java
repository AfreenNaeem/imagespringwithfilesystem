package com.example.imageSpring.images.seviceFileSys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.imageSpring.images.entityFileSys.ImageData;
import com.example.imageSpring.images.repoFileSys.FileDataRepo;
import com.example.imageSpring.images.repoFileSys.ImageRepository;
import com.example.imageSpring.images.utilFileSys.ImageUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.file.Path;
import java.nio.file.Paths;



@Service
public class ImageService {// class open

	@Autowired
	private ImageRepository repository;
	
	@Autowired
	private FileDataRepo fileDataRepo;
	
	@Autowired
	private JavaMailSender mailSender;


	public void sendSimpleMail(String toEmail, String subject, String body) throws MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true);
	    helper.setTo(toEmail);
	    helper.setSubject(subject);
	    helper.setText(body, true); // Set to true if you want to allow HTML content

	    // If no file is attached in this version, you can remove the attachment part
	    mailSender.send(message);
	}


	
	private final String FOLDERPATH =  "C:\\Users\\121\\Downloads\\FileStorage";

	private ImageData fileData;
	
	// Upload image method
	public String uploadImage(MultipartFile file) throws IOException {
	    ImageData image = new ImageData();
	    image.setName(file.getOriginalFilename());
	    image.setType(file.getContentType());
	    image.setImageData(ImageUtil.compressImage(file.getBytes()));

	    image = repository.save(image);

	    if (image != null) {
	        return "Image uploaded successfully!! " + file.getOriginalFilename();
	    }

	    return "Image failed to upload!!";
	}

	// Download image from the DB
	public byte[] downloadImage(String fileName) throws IOException {
	    Optional<ImageData> image = repository.findByName(fileName);

	    if (image.isPresent()) {
	        return ImageUtil.decompressImage(image.get().getImageData());
	    } else {
	        throw new FileNotFoundException("Image not found: " + fileName);
	    }
	}

	// Download image file from the file system by its name
	public org.springframework.core.io.Resource downloadImageFileByName(String fileName) throws IOException {
	    Optional<ImageData> imageOptional = repository.findByName(fileName);
	    if (imageOptional.isPresent()) {
	        ImageData image = imageOptional.get();
	        String filePath = image.getFilePath();

	        if (filePath == null) {
	            throw new RuntimeException("File path is missing for image: " + image.getName());
	        }

	        Path path = Paths.get(filePath);
	        org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

	        if (resource.exists() || resource.isReadable()) {
	            return resource;
	        } else {
	            throw new FileNotFoundException("File not readable or doesn't exist: " + fileName);
	        }
	    } else {
	        throw new FileNotFoundException("Image not found: " + fileName);
	    }
	}

	// Upload image to the file system
	public String uploadImageToFileSystem(MultipartFile file) throws IOException {
	    String filePath = FOLDERPATH + File.separator + file.getOriginalFilename();

	    File dest = new File(filePath);
	    file.transferTo(dest);

	    ImageData image = new ImageData();
	    image.setName(file.getOriginalFilename());
	    image.setType(file.getContentType());
	    image.setFilePath(filePath);

	    ImageData savedImage = repository.save(image);

	    if (savedImage != null) {
	        return "Image uploaded successfully! " + file.getOriginalFilename();
	    }
	    return "Image failed to upload!";
	}
	
	// Download image file from the file system
	public org.springframework.core.io.Resource downloadImageFile(String fileName) throws IOException {
	    Optional<ImageData> imageOptional = repository.findByName(fileName);

	    if (imageOptional.isPresent()) {
	        ImageData image = imageOptional.get();
	        String filePath = image.getFilePath();

	        if (filePath == null) {
	            throw new RuntimeException("File path is missing for image: " + image.getName());
	        }

	        Path path = Paths.get(filePath);
	        org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

	        if (resource.exists() || resource.isReadable()) {
	            return resource;
	        } else {
	            throw new FileNotFoundException("File not readable or doesn't exist: " + fileName);
	        }
	    } else {
	        throw new FileNotFoundException("Image not found: " + fileName);
	    }
	}

	// Optional method for further image handling (e.g., retrieving from the file system as byte array)
	public byte[] getImageFromFileSystem(String fileName) {
		// TODO: Implement this method if you want to retrieve the image as a byte array
		return null;
	}

	// Getters and Setters
	public FileDataRepo getFileDataRepo() {
		return fileDataRepo;
	}

	public void setFileDataRepo(FileDataRepo fileDataRepo) {
		this.fileDataRepo = fileDataRepo; 
	}

	public ImageData getFileData() {
		return fileData;
	}

	public void setFileData(ImageData fileData) {
		this.fileData = fileData;
	}

	public JavaMailSender getMailSender() {
	    return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
	    this.mailSender = mailSender;
	}

	
}// class close
