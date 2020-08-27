package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.vo.Permission;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission, Integer> {



    //通过角色id查询角色所拥有的所有权限
    List<Integer> queryPermissionByRole(Integer roleId);

    //通过角色id查询角色所拥有的所有权限的数量
    Integer queryPermissionCountByRoleId(Integer roleId);

    //通过角色id删除角色的所有的权限
    int deleteByRoleId(Integer roleId);

    //据用户ID查询用户拥有的角色对应的所有权限
    List<String> queryUserHasRolesHasPermissions(Integer userId);


    // 通过资源ID查询对应的权限
    Integer queryPermissionByModuleId(Integer mId);

    // 通过资源ID删除权限
    Integer deleteByModuleId(Integer mId);
}