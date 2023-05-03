package com.example.attendance.Service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.attendance.common.CheckAttendInfo;
import com.example.attendance.common.MyResult;
import com.example.attendance.dao.AttendInfo;
import com.example.attendance.dao.TemporAttendInfo;
import com.example.attendance.mapper.AttendMapper;
import com.example.attendance.mapper.TemporAttendMapper;
import com.example.attendance.repository.AttendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;



@Service
public class AttendService extends ServiceImpl<AttendMapper, AttendInfo> implements AttendRepo {


    private static final Double PERCENTAGEOFSIGNINFAILURES = 0.5;

    @Autowired
    private TemporAttendMapper temporAttendMapper;

    @Autowired
    private AttendMapper attendMapper;

    @Autowired
    private TemporAttendService temporAttendService;

    public MyResult<String> checkAttendInfo(String courseId) {
        MyResult<String> myResult  =  new MyResult<>();

        QueryWrapper<TemporAttendInfo> temporQueryWrapper = new QueryWrapper<>();
        temporQueryWrapper.eq("course_id", courseId);


        Map<Integer, CheckAttendInfo> checkAttendInfoMap = new HashMap<>();
        List<TemporAttendInfo> temporAttendInfoList  = temporAttendMapper.selectList(temporQueryWrapper);
        List<Integer> studentIdList = new ArrayList<>();

        for(TemporAttendInfo temporAttendInfo : temporAttendInfoList){
            if(!studentIdList.contains(temporAttendInfo.getId())){
                studentIdList.add(temporAttendInfo.getId());

                CheckAttendInfo checkAttendInfo = new CheckAttendInfo();
                CheckAttendInfo newCheckAttendInfo = checkAttendInfo(checkAttendInfo, temporAttendInfo);
                checkAttendInfoMap.put(temporAttendInfo.getId(),newCheckAttendInfo);
            }else{
                CheckAttendInfo haveInfo  = checkAttendInfoMap.get(temporAttendInfo.getId());
                CheckAttendInfo newCheckAttendInfo = checkAttendInfo(haveInfo, temporAttendInfo);
                checkAttendInfoMap.put(temporAttendInfo.getId(),newCheckAttendInfo);
            }

        }

        System.out.println(checkAttendInfoMap);
        for(Integer studentId : checkAttendInfoMap.keySet()){
            AttendInfo attendInfo = new AttendInfo();
            attendInfo.setId(studentId);
            attendInfo.setSignCourse(courseId);
            attendInfo.setSignStatus(1);
            int notSignTimes = checkAttendInfoMap.get(studentId).getNotSignTime();
            if(notSignTimes != 0){
                int checkTimes = checkAttendInfoMap.get(studentId).getCheckTimes();
                if((float)notSignTimes / checkTimes > PERCENTAGEOFSIGNINFAILURES){
                    attendInfo.setSignStatus(0);
                    String notSignDate = checkAttendInfoMap.get(studentId).getSignTime().replaceAll("null;", "");
                    attendInfo.setUnidentifiedTime(notSignDate);
                }
            }
            attendMapper.insert(attendInfo);
        }

        // Delete temporary record
        temporAttendService.delTemporInfo(courseId);

        return myResult;
    }

    public CheckAttendInfo checkAttendInfo(CheckAttendInfo haveInfo, TemporAttendInfo temporAttendInfo) {
        // save no sign info
        String signTime  = haveInfo.getSignTime();
        int notSignTime = haveInfo.getNotSignTime();
        if(temporAttendInfo.getSignStatus().equals("0")){

            signTime = signTime + ";" + dateFromatChange(temporAttendInfo.getSignTime().toString());
            notSignTime = notSignTime + 1;
        }

        haveInfo.setNotSignTime(notSignTime);
        haveInfo.setCheckTimes(haveInfo.getCheckTimes() + 1);
        haveInfo.setSignTime(signTime);
        return haveInfo;
    }

    public String dateFromatChange(String dateStr) {
        String result = null;
        if(dateStr != null){
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateStr,formatter1);
            result = formatter2.format(dateTime);
           
        }
        return result;
    }

}