package com.example.facerecognition.openfeign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@FeignClient("attendinfo")
@RequestMapping("/temporAttend")
public interface AttendFeign {

    @PostMapping("/add")
    void temporAttend(
            @RequestBody Map<String, String> allSignInfo,
                 @RequestParam String courseId,
                 @RequestParam String time);
}
