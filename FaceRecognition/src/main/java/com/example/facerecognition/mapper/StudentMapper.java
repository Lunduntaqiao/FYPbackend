package com.example.facerecognition.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.facerecognition.dao.StudentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StudentMapper extends BaseMapper<StudentInfo> {

    @Select("select student_name from student_info where id = #{id}")
    String findNameById(@Param("id") int id);


}
