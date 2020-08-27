package com.wse.crm.service;

import ch.qos.logback.core.net.AbstractSSLSocketAppender;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wse.crm.base.BaseService;
import com.wse.crm.dao.CusDevPlanMapper;
import com.wse.crm.dao.SaleChanceMapper;
import com.wse.crm.query.CusDevPlanQuery;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.vo.CusDevPlan;
import com.wse.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/21 0021 10:42
 */
@Service
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 通过营销id查询营销计划数据
     *
     * @param saleChanceId
     * @return
     */
    public Map<String, Object> selectCusDevPlanList(Integer saleChanceId) {
        HashMap<String, Object> map = new HashMap<>();

        // 查询指定营销机会的客户开发计划列表
        List<SaleChance> list = cusDevPlanMapper.selectCusDevPlanList(saleChanceId);


        map.put("code", 0);
        map.put("msg", "");
        map.put("count", list.size()); // 总记录数
        map.put("data", list); // 当前页显示的数据列表
        return map;
    }

    /**
     * 添加营销机会客户开发计划
     * 1、参数校验
     * saleChanceId营销机会ID  非空，且数据存在
     * planItem开发项名称       非空
     * planDate开发时间        非空
     * exeAffect开发影响       非空
     * 2. 设置默认值
     * createDate、updateDate、isValid
     * 3. 执行添加操作
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCusDevPlan(CusDevPlan cusDevPlan) {
        //1、参数校验
        checkCusDevPlanParams(cusDevPlan);
        //2、设置默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        //3、执行添加操作
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1, "客户开发计划添加失败！");

    }

    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        //验证saleChanceId是否存在，不存在抛出异常
        AssertUtil.isTrue(null == cusDevPlan.getSaleChanceId(), "数据异常，请重试！");
        //验证数据库中是否存在id为该id的营销机会
        AssertUtil.isTrue(null == saleChanceMapper.selectByPrimaryKey(cusDevPlan.getSaleChanceId()), "营销机会数据不存在！");
        //验证计划项内容不能为空，空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()), "计划项不能为空！");
        //验证计划时间不能为空，空则抛出异常
        AssertUtil.isTrue(null == cusDevPlan.getPlanDate(), "计划时间不能为空！");
        //验证执行效果不能为空，空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getExeAffect()), "执行效果不能为空！");
    }

    /**
     * 更新客户开发计划项
     * 1. 参数校验
     * 客户开发计划项id         非空判断，且数据存在
     * saleChanceId营销机会ID  非空，且数据存在
     * planItem开发项名称       非空
     * planDate开发时间        非空
     * exeAffect开发影响       非空
     * 2. 设置默认值
     * updateDate
     * 3. 执行修改操作
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan) {
        //1、参数校验
        AssertUtil.isTrue(null == cusDevPlan.getId(), "数据异常，请重试！");
        AssertUtil.isTrue(null == cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId()), "待更新计划项不存在！");
        checkCusDevPlanParams(cusDevPlan);
        //2、设置默认值
        cusDevPlan.setUpdateDate(new Date());

        //3、执行修改操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"更新失败，请重试！");
    }

    /**
     * 删除计划项的方法
     * @param id    计划项的id
     */
    public void deleteCusDevPlan(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(null==id ,"数据异常，请重试！");
        //通过id查询计划项对象
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        //更改计划项的属性值，isvalid值为0，不可见，假删除
        cusDevPlan.setIsValid(0);
        //设置修改时间
        cusDevPlan.setUpdateDate(new Date());

        //执行删除操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项删除失败");
    }


}
