package com.example.attendance.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.attendance.dao.AttendInfo;
import org.springframework.stereotype.Service;

@Service
public interface AttendRepo extends IService<AttendInfo> {
}
