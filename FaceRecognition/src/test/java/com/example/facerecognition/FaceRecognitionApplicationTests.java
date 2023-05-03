package com.example.facerecognition;

import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.equalizeHist;

@SpringBootTest
class FaceRecognitionApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testOpenCV() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //填你的图片地址
        Mat image = imread("E:\\Desktop\\OIP-C.jpg", 1);
        if (image.empty()){
            throw new Exception("image is empty!");
        }
        imshow("Original Image", image);
        List<Mat> imageRGB = new ArrayList<>();
        split(image, imageRGB);
        for (int i = 0; i < 3; i++) {
            equalizeHist(imageRGB.get(i), imageRGB.get(i));
        }
        merge(imageRGB, image);
        imshow("Processed Image", image);
        waitKey();
    }

}
