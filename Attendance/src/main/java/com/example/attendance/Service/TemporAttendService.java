package com.example.attendance.Service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.attendance.dao.TemporAttendInfo;
import com.example.attendance.mapper.TemporAttendMapper;
import com.example.attendance.repository.TemportAttendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class TemporAttendService extends ServiceImpl<TemporAttendMapper, TemporAttendInfo> implements TemportAttendRepo {

    @Autowired
    private TemporAttendMapper temporAttendMapper;

    @Autowired
    private TemportAttendRepo temportAttendRepo;

    public void temporAttend(Map<String, String> allSignInfo, String courseId,
                              String time){
        List<TemporAttendInfo> temporAttendInfoList = new ArrayList<>();

        for(String studentId : allSignInfo.keySet()){
            TemporAttendInfo temporAttendInfo = new TemporAttendInfo();
            temporAttendInfo.setId(Integer.valueOf(studentId));
            temporAttendInfo.setCourseId(courseId);
            temporAttendInfo.setSignStatus(allSignInfo.get(studentId));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = formatter.parse(time);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            temporAttendInfo.setSignTime(date);
            temporAttendInfoList.add(temporAttendInfo);
        }
        System.out.println(temporAttendInfoList);
        temportAttendRepo.saveBatch(temporAttendInfoList);
    }

    public void delTemporInfo(String courseId){
        QueryWrapper<TemporAttendInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        temporAttendMapper.delete(wrapper);

    }



}