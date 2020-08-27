package com.wse.crm.query;

import com.wse.crm.base.BaseQuery;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/21 0021 15:32
 */
public class UserQuery extends BaseQuery {
    // ⽤户名
    private String userName;
    // 邮箱
    private String email;
    // 电话
    private String phone;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

