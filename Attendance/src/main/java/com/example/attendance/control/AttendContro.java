package com.example.attendance.control;


import com.example.attendance.Service.AttendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@CrossOrigin
@RestController
@RequestMapping("/attend")
public class AttendContro {


    @Autowired
    private AttendService attendService;

    @PostMapping("/add")
    public void checkAttendInfo(@RequestParam(name = "courseId") String courseId) {
        attendService.checkAttendInfo(courseId);
    }
}
