package com.wse.crm.query;

import com.wse.crm.base.BaseQuery;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 15:25
 */
public class RoleQuery extends BaseQuery {
    // ⻆⾊名
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
