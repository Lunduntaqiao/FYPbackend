package com.example.attendance.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CheckAttendInfo {
    private int notSignTime;
    private int checkTimes;
    /** sign_time; Record the undetected time */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING, timezone="GMT+8")
    private String signTime;

}
