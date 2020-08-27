package com.wse.crm.service;

import com.wse.crm.base.BaseService;
import com.wse.crm.dao.PermissionMapper;
import com.wse.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/25 0025 21:47
 */
@Service
public class PermissionService extends BaseService<Permission,Integer> {
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 根据用户ID查询用户拥有的角色对应的所有权限
     * @param userId
     * @return
     */
    public List<String> queryUserHasRolesHasPermissions(Integer userId) {

        return permissionMapper.queryUserHasRolesHasPermissions(userId);

    }
}
