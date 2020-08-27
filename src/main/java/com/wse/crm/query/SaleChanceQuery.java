package com.wse.crm.query;

import com.wse.crm.base.BaseQuery;

/**
 * 营销机会管理多条件查询条件
 *
 * @author wse
 * @version 1.0
 * @date 2020/8/20 0020 9:45
 */
public class SaleChanceQuery extends BaseQuery {
    private String customerName; // 客户名称
    private String createMan; // 创建⼈
    private Integer state; // 分配状态

    //开发状态
    private Integer devResult;
    //分配人
    private Integer assignMan;

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
