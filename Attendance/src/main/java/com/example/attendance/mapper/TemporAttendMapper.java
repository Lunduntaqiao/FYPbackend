package com.example.attendance.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.dao.TemporAttendInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Mapper
@Repository
public interface TemporAttendMapper extends BaseMapper<TemporAttendInfo> {


}
