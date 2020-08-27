package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    Integer selectUserRoleCountByUserId(Integer userId);

    Integer deleteRelationByUserId(Integer userId);
}