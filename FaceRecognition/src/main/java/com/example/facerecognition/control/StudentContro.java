package com.example.facerecognition.control;



import com.example.facerecognition.Service.StudentService;
import com.example.facerecognition.common.MyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/Recog")
public class StudentContro {

    @Autowired
    private StudentService recogService;

    @PostMapping("/faceRecog")
    public MyResult<Map<String, String>> faceRecog(@RequestParam(name = "courseId") String courseId,
                                                   @RequestParam(name = "courseId") int cameraId) throws IOException {
        return recogService.faceRecogRequest(courseId, cameraId);
    }

    @PostMapping("/faceRecog1")
    public MyResult<Map<String, String>> faceRecog1(@RequestParam(name = "image") MultipartFile image,
                                                    @RequestParam(name = "courseId") String courseId) throws IOException {
        return recogService.faceRecogRequest1(image, courseId);
    }
    @GetMapping("/getCameraInfo")
    public Map<Integer, String> getCameraInfo() {
        return recogService.getCameraInfo();
    }


    @GetMapping("/performanceTest")
    public Map<String, Double> performanceTest(){
        return recogService.performanceTest();
    }

    @GetMapping("/comparisonTest")
    public Map<String, Double> comparisonTest(){
        return recogService.comparisonTest();
    }
}
