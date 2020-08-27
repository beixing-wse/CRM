package com.wse.crm.service;

import com.wse.crm.base.BaseService;
import com.wse.crm.dao.UserMapper;
import com.wse.crm.dao.UserRoleMapper;
import com.wse.crm.model.UserModel;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 13:48
 */
@Service
public class UserRoleService extends BaseService<UserRole,Integer> {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 用户角色关联
     *      1. 添加操作
     *          原始数据不存在，直接添加新的数据
     *      2. 修改操作
     *          原始数据不存在，直接添加新的数据
     *          原始数据存在
     *              1. 添加了新的选项
     *              2. 移除了旧的选项
     *              3. 添加了新的角色，又移除了旧的角色
     *              4. 移除了所有
     *      如果解决角色分配的问题？
     *          先删除与当前用户相关联的所有数据，再将新添加的所有角色信息绑定
     * @param userId
     * @param roleIds
     */
    public void relaionUserRole(Integer userId, String roleIds) {
        // 判断用户Id
        AssertUtil.isTrue(null == userId || userMapper.selectByPrimaryKey(userId) == null, "用户不存在，请重试！");
        // 通过用户Id查询对应的用户角色关联信息
        Integer count =  userRoleMapper.selectUserRoleCountByUserId(userId);
        // 判断是否存在当前用户的用户关联信息
        if (count > 0) {
            // 如果存在，则删除原有的关联数据
            AssertUtil.isTrue(userRoleMapper.deleteRelationByUserId(userId) != count,"用户角色关联失败！");
        }

        // 判断用户是否添加了角色关联数据
        if (StringUtils.isNotBlank(roleIds)) {
            // 添加新的关联数据
            List<UserRole> userRoleList = new ArrayList<>();
            // 将字符串转换为数据
            String[] roleIdArr = roleIds.split(",");
            // 遍历角色ID的数组
            for(String roleId : roleIdArr) {
                UserRole userRole = new UserRole();
                userRole.setUpdateDate(new Date());
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setCreateDate(new Date());
                // 将对象设置到集合中
                userRoleList.add(userRole);
            }

            // 批量添加用户角色
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "用户角色关联失败！");
        }
    }

}
