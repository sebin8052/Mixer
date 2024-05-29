package com.Mixer.library.utils;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class ImageUpload
{
    //    String rootPath = System.getProperty("user.home");

    String UPLOAD_FOLDER = "/Admin/src/main/resources/static/imgs/images";

    String UPLOAD_FOLDER_CUSTOMER ="/Customer/src/main/resources/static/imgs/images";


    public String storeFile(MultipartFile file) throws IOException
    {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Paths.get(UPLOAD_FOLDER);
        Path uploadPathCustomer = Paths.get(UPLOAD_FOLDER_CUSTOMER);


        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            Files.createDirectories(uploadPathCustomer);
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            Path filePath = uploadPath.resolve(fileName);
            Path filePathCustomer = uploadPathCustomer.resolve(fileName);
            Files.write(filePath,buffer, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(filePathCustomer,buffer, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);

            return fileName.toString();
        } catch (IOException e) {
            throw new IOException("Could not store file " + fileName, e);
        }
    }
    public boolean checkExist(MultipartFile File){
        boolean isExist = false;
        try {
            java.io.File file = new File(UPLOAD_FOLDER +"/" + File.getOriginalFilename());
            isExist = file.exists();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }

    /* cropping the images */
    public byte[] cropImage(MultipartFile file, int x, int y, int width, int height) throws IOException
    {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage croppedImage = Scalr.crop(originalImage, x, y, width, height);

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || extension.isEmpty())
        {
            extension = "png";
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(croppedImage, extension, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Could not crop file " + file.getOriginalFilename(), e);
        }
    }
//    store crop images
    public String storeCroppedFile(byte[] croppedImageBytes, String fileName) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_FOLDER);
        Path uploadPathCustomer = Paths.get(UPLOAD_FOLDER_CUSTOMER);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            Files.createDirectories(uploadPathCustomer);
        }

        try {
            Path filePath = uploadPath.resolve(fileName);
            Path filePathCustomer = uploadPathCustomer.resolve(fileName);
            Files.write(filePath, croppedImageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(filePathCustomer, croppedImageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Log the saved file path
            System.out.println("Cropped image saved to: " + filePathCustomer);

            return fileName;
        } catch (IOException e) {
            throw new IOException("Could not store cropped file " + fileName, e);
        }
    }
}
