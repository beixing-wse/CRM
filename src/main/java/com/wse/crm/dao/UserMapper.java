package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.vo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {


    //@Select("select * from t_user where user_name = #{userName}")
    //通过用户名查询用户信息
    User queryUserByName( String userName);

    //通过用户id修改用户密码
    int updateUserPwd(@RequestParam("userId") Integer userId,@RequestParam("userPwd") String userPwd);

    //查询所有的销售人员
     List<Map<String,Object>> queryAllSales();




}
