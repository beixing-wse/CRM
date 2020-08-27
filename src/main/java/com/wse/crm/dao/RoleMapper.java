package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    // 查询⻆⾊列表
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    Role queryRoleByRoleName(String roleName);
}