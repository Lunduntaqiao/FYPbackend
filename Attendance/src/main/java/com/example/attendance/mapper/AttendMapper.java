package com.example.attendance.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.dao.AttendInfo;
import org.apache.ibatis.annotations.Mapper;

import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AttendMapper extends BaseMapper<AttendInfo> {

}
