package com.example.facerecognition.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName(value = "student_info")
public class StudentInfo {
    private int id;
    private String studentName;
    private String courseId ;
    private String studentFacePath ;
}
