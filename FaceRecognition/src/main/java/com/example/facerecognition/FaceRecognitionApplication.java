package com.example.facerecognition;

import com.example.facerecognition.common.MyResult;
import org.mybatis.spring.annotation.MapperScan;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.facerecognition.mapper")
@EnableFeignClients
public class FaceRecognitionApplication {
    static {
        ClassPathResource resource = new ClassPathResource("opencv/x64/opencv_java460.dll");
        File libraryFile = null;
        try {
            libraryFile = File.createTempFile("opencv/x64/opencv_java460.dll", null);
            libraryFile.deleteOnExit();
            Files.copy(resource.getInputStream(), libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.load(libraryFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        }


    public static void main(String[] args) {
        SpringApplication.run(FaceRecognitionApplication.class, args);
    }


}
