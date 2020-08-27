package com.wse.crm.controller;

import com.wse.crm.base.BaseController;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.query.CusDevPlanQuery;
import com.wse.crm.service.CusDevPlanService;
import com.wse.crm.service.SaleChanceService;
import com.wse.crm.vo.CusDevPlan;
import com.wse.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/21 0021 9:33
 */
@RequestMapping("cus_dev_plan")
@Controller
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 进入客户开发页面
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "cusDevPlan/cus_dev_plan";
    }


    /**
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(HttpServletRequest request,Integer sid){
        //开打的页面需要显示营销机会的数据
        //通过前台传过来的id，查询营销机会的数据
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sid);
        //将数据设置到作用域中
        request.setAttribute("saleChance",saleChance);

        //跳转到开发数据详情页面
        return "cusDevPlan/cus_dev_plan_data";
    }


    /**
     * 查询指定营销机会的客户开发项列表
     * @param saleChanceId
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectCusDevPlanList(Integer saleChanceId) {
        return cusDevPlanService.selectCusDevPlanList(saleChanceId);
    }


    /**
     * 添加客户开发计划的方法
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo saveCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.saveCusDevPlan(cusDevPlan);
        return success("计划项添加成功");
    }

    /**
     * 更新客户开发计划的方法
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功");
    }

    /**
     * 跳转添加计划项页面
     * @param cId
     * @param saleChanceId
     * @param request
     * @return
     */
    @RequestMapping("toAddOrUpdateCusDevPlanPage")
    public String toAddOrUpdateCusDevPlanPage(Integer cId,Integer saleChanceId, HttpServletRequest request){
        // 判断cId是否为空。如果不为空，则为更新操作，通过id查询客户开发计划对象
        if (cId != null) {
            CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(cId);
            // 设置作用域
            request.setAttribute("cusDevPlan", cusDevPlan);

        }

        // cId为空，为添加操作，将营销机会的ID设置到作用域中
        request.setAttribute("saleChanceId", saleChanceId);
        return "cusDevPlan/add_update";
    }

    @RequestMapping("delete")
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项删除成功");
    }


}
