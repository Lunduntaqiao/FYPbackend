package com.example.attendance.control;


import com.example.attendance.Service.TemporAttendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping("/temporAttend")
public class TemporAttendContro {

    @Autowired
    private TemporAttendService temporAttendService;
    /**
     * feignContro
     */
    @PostMapping("/add")
    public void temporAttend(
            @RequestBody Map<String, String> allSignInfo,
                             @RequestParam(name = "courseId") String courseId,
                             @RequestParam(name = "time") String time){
        temporAttendService.temporAttend(allSignInfo, courseId, time);
    }

    @PostMapping("/del")
    public void temporDelete(
                             @RequestParam(name = "courseId") String courseId){
        temporAttendService.delTemporInfo( courseId);
    }

}
