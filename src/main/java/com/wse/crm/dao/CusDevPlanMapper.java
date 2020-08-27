package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.query.CusDevPlanQuery;
import com.wse.crm.vo.CusDevPlan;
import com.wse.crm.vo.SaleChance;

import java.util.List;
import java.util.Map;

public interface CusDevPlanMapper extends BaseMapper<CusDevPlan, Integer> {

    // 查询指定营销机会的客户开发计划列表
    List<SaleChance> selectCusDevPlanList(Integer saleChanceId);
}