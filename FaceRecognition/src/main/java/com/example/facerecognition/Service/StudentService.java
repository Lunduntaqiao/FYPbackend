package com.example.facerecognition.Service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.facerecognition.common.MyResult;
import com.example.facerecognition.dao.StudentInfo;
import com.example.facerecognition.mapper.StudentMapper;
import com.example.facerecognition.openfeign.AttendFeign;
import com.example.facerecognition.repository.StudentRepo;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.dnn.Dnn;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.opencv.dnn.Net;
import org.tensorflow.SavedModelBundle;

@Service
public class StudentService extends ServiceImpl<StudentMapper, StudentInfo> implements StudentRepo {

    /**
     * face recognition by no cover
     */
    private String detectionWay = "opencv/resources/haarcascade_frontalface_alt.xml";

    private String identificationWay = "opencv/resources/openface.nn4.small2.v1.t7";

    private String testPath = "lfw";

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private AttendFeign attendFeign;

    private CascadeClassifier classifier;

    private Net net;

    public Map<Integer, String> getCameraInfo() {
        Map<Integer, String> cameraList = new HashMap<>();
        // Loop through all possible camera indices to find available cameras
        for (int i = 0; i < 10; i++) {
            VideoCapture capture = new VideoCapture(i);
            if (capture.isOpened()) {
                // Successfully opened the camera, so print its index
                cameraList.put(i, capture.getBackendName());

                // Release the camera capture to free up resources
                capture.release();
            }
        }
        return cameraList;
    }

    public MyResult<Map<String, String>> faceRecogRequest(String courseId, int cameraId) {
        // get camera
        VideoCapture videoCapture = new VideoCapture(cameraId);
        Mat frame = new Mat();
        videoCapture.read(frame);
        Imgcodecs.imwrite("class.jpg", frame);
        videoCapture.release();

        return faceRecog(frame, courseId);
    }

    public MyResult<Map<String, String>> faceRecogRequest1(MultipartFile image, String courseId) throws IOException {
        byte[] bytes = image.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        MatOfByte matOfByte = new MatOfByte(bytes);
        Mat frame = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);

        return faceRecog(frame, courseId);
    }


    public MyResult<Map<String, String>> faceRecog(Mat frame, String courseId) {
        MyResult<Map<String, String>> myResult = new MyResult<>();

        // get time
        String time = nowDateTime();

        File libraryFile = fileCall(detectionWay);
        classifier = new CascadeClassifier(libraryFile.getAbsolutePath());

        ClassLoader classLoader = getClass().getClassLoader();
        String modelPath = Objects.requireNonNull(classLoader.getResource(identificationWay)).getPath();
        File file = new File(modelPath);
        net = Dnn.readNet(file.getAbsolutePath());


        // check face
        MatOfRect face = new MatOfRect();
        classifier.detectMultiScale(frame, face);

        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("course_id", courseId);
        queryWrapper.select("id", "student_face_path", "student_name");

        List<StudentInfo> studentIdAndFacePath = studentMapper.selectList(queryWrapper);

        // all this course student info
        Map<String, String> allSignInfo = new HashMap<>();

        for (int i = 0; i < studentIdAndFacePath.size(); i++) {
            allSignInfo.put(String.valueOf(studentIdAndFacePath.get(i).getId()), "0");
        }

        int i = 1;
        for (Rect rect : face.toArray()) {
            //imageCut(frame, "E:\\Desktop\\face\\" + i + ".jpg", rect.x, rect.y, rect.width, rect.height);
            int studentId = 0;
            Rect faceRect = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat facePicture = new Mat(frame, faceRect);
            studentId = faceRecogHelp(studentIdAndFacePath, facePicture);
            // Picture frame
            Imgproc.rectangle(frame, faceRect, new Scalar(0, 255, 0));


            // recog true then add in tem attendance
            if (studentId != 0) {
                // set Name
                String studentName = studentMapper.findNameById(studentId);
                Imgproc.putText(frame, studentName, new Point(rect.x, rect.y), Imgproc.FONT_HERSHEY_SIMPLEX,
                        1.0, new Scalar(255, 0, 0), 1, Imgproc.LINE_AA, false);
                allSignInfo.put(String.valueOf(studentId), "1");
            }
            i++;
        }
        Imgcodecs.imwrite("class.jpg", frame);
        attendFeign.temporAttend(allSignInfo, courseId, time);
        myResult.setMessage("Number of face recognition " + face.toArray().length);
        myResult.setData(allSignInfo);


        return myResult;
    }


    public String nowDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }


    public int faceRecogHelp(List<StudentInfo> studentIdAndFacePath, Mat facePicture) {

        double ArecogAns = 0.0;
        int ansNum = -1;
        // compare
        for (int i = 0; i < studentIdAndFacePath.size(); i++) {
            String facePath = studentIdAndFacePath.get(i).getStudentFacePath();
            ArrayList<String> allFacePath = getAllFile(facePath);

            for (String path : allFacePath) {

                Mat faceFromLib = Imgcodecs.imread(path);
                double chackAns = compare(facePicture, faceFromLib);

                if (chackAns > 0.88) {
                    if (chackAns > ArecogAns) {
                        ArecogAns = chackAns;
                        ansNum = i;
                    }
                }
            }

        }
        if (ansNum == -1) {
            return 0;
        } else {
            return studentIdAndFacePath.get(ansNum).getId();
        }

    }

    public double compare(Mat img1, Mat img2) {

        // Extract feature vectors
        Mat feature1 = extractFeature(img1);
        Mat feature2 = extractFeature(img2);

        // Compute similarity
//        double distance = Core.norm(feature1, feature2);
        if (feature1.empty() || feature2.empty()) {
            return 0;
        } else {
            // 计算点乘
            feature1.convertTo(feature1, CvType.CV_32F);
            feature2.convertTo(feature2, CvType.CV_32F);
            double dotProduct = feature1.dot(feature2);
//        Mat result = new Mat();
//        Core.gemm(feature1, feature2, 1, new Mat(), 0, result, 0);
//        double dotProduct = result.get(0, 0)[0];
            // 计算模长
            double normA = Core.norm(feature1);
            double normB = Core.norm(feature2);

            // 计算余弦相似度
            return dotProduct / (normA * normB);
        }

    }

    /**
     * 提取特征向量
     * @param img
     * @return
     */
    public Mat extractFeature(Mat img) {
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);


        // 检测人脸
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(gray, faces);


        // 提取特征向量
        Mat features = new Mat();
        for (Rect rect : faces.toArray()) {
            Mat face = new Mat(gray, rect);

            // 创建一个3通道图像
            Mat colorImage = new Mat(face.rows(), face.cols(), CvType.CV_8UC3);

            // 复制灰度图像的值到每个通道
            List<Mat> channels = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                channels.add(face);
            }
            Core.merge(channels, colorImage);

            Imgproc.resize(colorImage, colorImage, new Size(96, 96));
            Mat blob = Dnn.blobFromImage(colorImage, 1.0, new Size(96, 96), new Scalar(0, 0, 0), false, false);
            net.setInput(blob);
            Mat feature = net.forward();
            features.push_back(feature.reshape(1, 1));
        }


        return features;
    }

    /**
     * get face lib
     *
     * @param facePath
     * @return facePath
     */
    public ArrayList<String> getAllFile(String facePath) {
        ArrayList<String> allFacePath = new ArrayList<>();
        File file = new File(facePath);
        File[] array = file.listFiles();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].isDirectory()) {
                    getAllFile(array[i].getPath());
                } else {
                    allFacePath.add(array[i].getPath());
                }

            }
        }
        return allFacePath;
    }

    /**
     * cut face
     *
     * @param image
     * @param outPath
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void imageCut(Mat image, String outPath, int x, int y, int width, int height) {

        Rect rect = new Rect(x, y, width, height);
        // Mat sub = new Mat(image,rect);
        Mat sub = image.submat(rect);
        Mat mat = new Mat();
        Size size = new Size(width, height);

        Imgproc.resize(sub, mat, size);
        Imgcodecs.imwrite(outPath, mat);
    }


    public File fileCall(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        File libraryFile = null;
        try {
            libraryFile = File.createTempFile(path, null);
            libraryFile.deleteOnExit();
            Files.copy(resource.getInputStream(), libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return libraryFile;
    }


    /**
     * Performance test
     */
    public Map<String, Double> performanceTest() {
        Map<String, Double> result = new HashMap<>();
        File testFolder = new File(testPath);
        File libraryFile = fileCall(detectionWay);

        classifier = new CascadeClassifier(libraryFile.getAbsolutePath());

        int numTests = 0;
        int truePositives = 0, falsePositives = 0, falseNegatives = 0;
        for (File fileList : Objects.requireNonNull(testFolder.listFiles())) {
            for (File file : Objects.requireNonNull(fileList.listFiles())) {
                Mat image = Imgcodecs.imread(file.getAbsolutePath());
                MatOfRect faces = new MatOfRect();
                classifier.detectMultiScale(image, faces);
                if (faces.toArray().length != 0) {
                    if (faces.toArray().length == 1) {
                        truePositives++;
                    } else {
                        falsePositives++;
                    }
                } else {
                    falseNegatives++;
                }

                numTests++;
            }
        }

//        System.out.println("truePositives: " + truePositives);
//        System.out.println("falsePositives: " + falsePositives);
//        System.out.println("falseNegatives: " + falseNegatives);
//        System.out.println("numTests: " + numTests);
        double accuracy = (double) truePositives / (truePositives + falsePositives + falseNegatives);
        double recall = (double) truePositives / (truePositives + falseNegatives);
        double precision = (double) truePositives / (truePositives + falsePositives);
        double f1 = 2 * precision * recall / (precision + recall);
//
//        System.out.println("Accuracy: " + accuracy);
//        System.out.println("Recall: " + recall);
//        System.out.println("Precision: " + precision);
//        System.out.println("F1 Score: " + f1);
        result.put("Accuracy", accuracy);
        result.put("Recall", recall);
        result.put("Precision", precision);
        result.put("F1 Score", f1);
        return result;
    }


    /**
     * Comparison test
     */
    public Map<String, Double> comparisonTest() {
        Map<String, Double> result = new HashMap<>();
        File testFolder = new File(testPath);
        File libraryFile = fileCall(detectionWay);

        classifier = new CascadeClassifier(libraryFile.getAbsolutePath());

        ClassLoader classLoader = getClass().getClassLoader();
        String modelPath = Objects.requireNonNull(classLoader.getResource(identificationWay)).getPath();
        File modelfile = new File(modelPath);
        net = Dnn.readNet(modelfile.getAbsolutePath());

        int numTests = 0;
        int truePositives = 0, falsePositives = 0, falseNegatives = 0;
        List<String> allFilePath = new ArrayList<>();
        // only one face in the picture
        for (File fileList : Objects.requireNonNull(testFolder.listFiles())) {
            for (File file : Objects.requireNonNull(fileList.listFiles())) {
                Mat image = Imgcodecs.imread(file.getAbsolutePath());
                MatOfRect faces = new MatOfRect();
                classifier.detectMultiScale(image, faces);
                if (faces.toArray().length != 0) {
                    if (faces.toArray().length == 1) {
                        for (Rect rect : faces.toArray()) {

                            Rect faceRect = new Rect(rect.x, rect.y, rect.width, rect.height);
                            Mat facePicture = new Mat(image, faceRect);

                            for (File compareFileList : Objects.requireNonNull(testFolder.listFiles())) {
                                for (File compareFile : Objects.requireNonNull(compareFileList.listFiles())) {
                                    Mat faceFromLib = Imgcodecs.imread(compareFile.getAbsolutePath());

                                    MatOfRect compareFaces = new MatOfRect();
                                    classifier.detectMultiScale(faceFromLib, compareFaces);
                                    if (compareFaces.toArray().length == 1) {
                                        double chackAns = compare(facePicture, faceFromLib);

                                        if (chackAns > 0.88) {
                                            if (file.getAbsolutePath().equals(compareFile.getAbsolutePath())) {
                                                truePositives++;
                                            } else {
                                                falseNegatives++;
                                            }
                                        } else {
                                            falseNegatives++;
                                        }
                                    }


                                }
                            }
                        }

                        numTests++;
                    }
                }
            }
        }


        double accuracy = (double) truePositives / (truePositives + falsePositives + falseNegatives);
        double recall = (double) truePositives / (truePositives + falseNegatives);
        double precision = (double) truePositives / (truePositives + falsePositives);
        double f1 = 2 * precision * recall / (precision + recall);

        System.out.println("Accuracy: " + accuracy);
        System.out.println("Recall: " + recall);
        System.out.println("Precision: " + precision);
        System.out.println("F1 Score: " + f1);
        System.out.println("numTests: " + numTests);

        result.put("Accuracy", accuracy);
        result.put("Recall", recall);
        result.put("Precision", precision);
        result.put("F1 Score", f1);
        return result;

    }
}
