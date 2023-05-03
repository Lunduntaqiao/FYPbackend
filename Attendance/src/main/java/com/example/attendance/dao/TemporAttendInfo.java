package com.example.attendance.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@TableName(value = "tempor_attend")
public class TemporAttendInfo {
    private Integer id ;
    private String courseId;
    private String signStatus;
    /** sign_time; Record the undetected time */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING, timezone="GMT+8")
    private Date signTime ;

}
