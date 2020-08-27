package com.wse.crm.controller;

import com.wse.crm.annotation.RequirePermission;
import com.wse.crm.base.BaseController;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.query.SaleChanceQuery;
import com.wse.crm.service.SaleChanceService;
import com.wse.crm.service.UserService;
import com.wse.crm.util.LoginUserUtil;
import com.wse.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 * @author wse
 * @version 1.0
 * @date 2020/8/20 0020 10:13
 */
@Controller
@RequestMapping("/sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private UserService userService;


    /**
     * 进入营销机会页面
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     * 多条件分⻚查询营销机会
     * @param query
     * @param flag      标识（flag=1 代表当前查询为开发计划数据，设置查询分配⼈参数）
     * @param request   请求
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequirePermission(code = "101001")
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query,Integer flag,HttpServletRequest request){

        // 查询参数flag=1 代表当前查询为开发计划数据，设置查询分配⼈参数
        if (null!=flag&&flag==1){
            // 分配状态为已分配，且分配人是当前登录用户自己
            query.setState(1); // 已分配
            // 通过cookie中获取当前登录用户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            query.setAssignMan(userId);

        }

        return saleChanceService.querySaleChanceByParams(query);

    }




    /**
     * 添加营销机会
     * @param saleChance
     * @param request
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    @RequirePermission(code = "101002")
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        // 获取⽤户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 获取⽤户的真实姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        // 设置营销机会的创建⼈
        saleChance.setCreateMan(trueName);
        // 添加营销机会的数据
        saleChanceService.addSaleChance(saleChance,request);
        return success("营销机会数据添加成功");
    }


    /**
     * 修改营销机会的方法
     * @param saleChance
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    @RequirePermission(code = "101004")
    public ResultInfo updateSaleChance(SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功!");
    }


    /**
     * 进入添加/修改营销机会页面
     * @return
     */
    @RequestMapping("toSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer saleChanceId,HttpServletRequest request){


        if (null!=saleChanceId){
            // 通过Id查询营销机会对象
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            // 设置数据到请求域
            request.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    @RequirePermission(code = "101003")
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success();
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @RequestMapping("updateDevResult")
    @ResponseBody
    public ResultInfo updateDevResult(Integer id,Integer devResult){
        saleChanceService.updateDevResult(id,devResult);
        return success("开发状态更新成功！");
    }


}
