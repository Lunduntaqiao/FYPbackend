package com.example.facerecognition.repository;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.facerecognition.dao.StudentInfo;
import org.springframework.stereotype.Service;

@Service
public interface StudentRepo extends IService<StudentInfo> {
}
