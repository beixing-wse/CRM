package com.wse.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wse.crm.base.BaseService;
import com.wse.crm.dao.SaleChanceMapper;
import com.wse.crm.enums.DevResult;
import com.wse.crm.enums.StateStatus;
import com.wse.crm.query.SaleChanceQuery;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.util.CookieUtil;
import com.wse.crm.util.PhoneUtil;
import com.wse.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/20 0020 9:34
 */
@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分⻚查询营销机会(BaseService 中有对应的⽅法)
     *
     * @param query
     * @return
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<SaleChance> list = saleChanceMapper.selectByParams(query);
        // 得到分页数据
        PageInfo<SaleChance> pageInfo = new PageInfo<>(list);
        //设置返回浏览器的格式
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", list.size());
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 添加营销机会数据
     *      1. 参数校验 （SaleChance对象）
     *          customerName客户名     非空
     *          linkMan联系人          非空
     *          linkPhone手机号        非空，格式正确
     *      2. 设置参数的默认值
     *          createDate创建时间      默认当前系统时间
     *          updateDate修改时间      默认当前系统时间
     *          isValid是否有效         默认1=有效（1=有效，0=无效）
     *          createMan创建人         默认是当前登录用户名（用户名称）
     *          assignMan分配人
     *
     *          如果有值，表示已分配（用户ID）
     *              state分配状态       1=已分配 （1=已分配，0=未分配）
     *              assignTime分配时间  默认当前系统时间
     *              devResult开发状态   1=开发中 （0=未开发，1=开发中，2=开发成功，3=开发失败）
     *          如果没值，表示未分配（null）
     *              state分配状态       0=未分配 （1=已分配，0=未分配）
     *              assignTime分配时间  null
     *              devResult开发状态   0=未开发 （0=未开发，1=开发中，2=开发成功，3=开发失败）
     *      3. 执行添加操作，判断受影响的行数
     *
     * @param saleChance
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance, HttpServletRequest request) {
        //验证营销机会数据的参数
        checkSaleChanceParams(saleChance);

        //营销机会参数正常执行以下代码

        //设置参数默认值
        //createDate创建时间（默认系统当前时间）
        saleChance.setCreateDate(new Date());
        //updateDate修改时间（默认系统当前时间）
        saleChance.setUpdateDate(new Date());
        //isValid是否有效（默认有效，值为1）
        saleChance.setIsValid(1);
        //createMan创建人（默认为当前登入的用户名），通过cookic获取
        String userName = CookieUtil.getCookieValue(request, "userName");
        saleChance.setCreateMan(userName);

        //判断是否有分配人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            //分配人为空，未分配，分配状态state值为0
            saleChance.setState(0);
            // assignTime分配时间  null
            saleChance.setAssignTime(null);
            // devResult开发状态   0=未开发 （0=未开发，1=开发中，2=开发成功，3=开发失败）
            saleChance.setDevResult(0);
        } else {
            //分配人不为空，已分配，分配状态state值为1
            saleChance.setState(1);
            //assignTime分配时间 （默认系统当前时间）
            saleChance.setAssignTime(new Date());
            // devResult开发状态   1=开发中 （0=未开发，1=开发中，2=开发成功，3=开发失败）
            saleChance.setDevResult(1);
        }

        //执行添加操作，判断影响行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1, "营销机会添加失败");

    }

    /**
     * 营销机会的参数校验（SaleChance）
     *
     * @param saleChance
     */
    private void checkSaleChanceParams(SaleChance saleChance) {
        //验证客户姓名是否为空，空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getCustomerName()), "客户名称不能为空");
        //验证客户联系人是否为空，空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkMan()), "联系人不能为空");
        //判断手机号码是否为空，空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkPhone()), "联系人号码不能为空");
        //判断手机号是否合法，不合法则抛出异常
        AssertUtil.isTrue(!PhoneUtil.isMobile(saleChance.getLinkPhone()), "手机号码格式不正确");
    }


    /**
     * 营销机会更新操作
     *      1. 参数校验
     *          id营销机会ID           非空，数据库中数据存在
     *          customerName客户名     非空
     *          linkMan联系人          非空
     *          linkPhone手机号        非空，格式正确
     *      2. 设置参数的默认值
     *          updateDate修改时间      默认当前系统时间
     *          assignMan分配人
     *          1. 改之前未分配  改之后未分配
     *
     *          2. 改之前未分配  改之后已分配
     *              state分配状态       1=已分配 （1=已分配，0=未分配）
     *              assignTime分配时间  默认当前系统时间
     *              devResult开发状态   1=开发中 （0=未开发，1=开发中，2=开发成功，3=开发失败）
     *          3. 改之前已分配   改之后未分配
     *              state分配状态       0=未分配 （1=已分配，0=未分配）
     *              assignTime分配时间  null
     *              devResult开发状态   0=未开发 （0=未开发，1=开发中，2=开发成功，3=开发失败）
     *          4. 改之前已分配   改之后已分配
     *              判断修改前后是否是同一个分配人，如果不是同一个人，更新分配时间
     *      3. 执行更新操作，判断受影响的行数
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        //判断传进来的SaleChance的id是否为null，为null时抛出异常
        AssertUtil.isTrue(null == saleChance.getId(), "系统异常，请重试！");
        //通过id查询数据库中是否存在该条数据
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断该条数据是否存在，不存在则抛出异常
        AssertUtil.isTrue(null == temp, "待更新用户不存在");

        //参数校验
        checkSaleChanceParams(saleChance);

        //设置参数默认值
        saleChance.setUpdateDate(new Date());

        //判断是否存在分配人
        // 不存在分配人
        if (StringUtils.isBlank(temp.getAssignMan())) {
            //修改后已分配
            if (StringUtils.isNotBlank(saleChance.getAssignMan())) {
                //state分配状态值为1，枚举值   1=已分配 （1=已分配，0=未分配）
                saleChance.setState(StateStatus.STATED.getType());
                //assignTime分配时间（默认系统当前时间）
                saleChance.setAssignTime(new Date());
                //devResult开发状态值为1开发中（0=未开发，1=开发中，2=开发成功，3=开发失败）
                saleChance.setState(DevResult.DEVING.getStatus());
            }
            //修改后认为未分配无需操作
        } else {
            //修改前已分配
            //如果修改后未分配
            if (StringUtils.isBlank(saleChance.getAssignMan())) {
                //state分配状态值为0
                saleChance.setState(StateStatus.UNSTATE.getType());
                //assignTime分配时间为null
                saleChance.setAssignTime(null);
                //devResult开发状态值为0开发中（0=未开发，1=开发中，2=开发成功，3=开发失败）
                saleChance.setDevResult(DevResult.UNDEV.getStatus());

            } else {
                //修改后已分配,判断修改前后是否为同一个用户
                if (saleChance.getAssignMan().equals(temp.getAssignMan())) {
                    //如果是同一个用户，分配时间修改为修改器前的分配时间
                    saleChance.setAssignTime(temp.getAssignTime());
                } else {
                    // 更新分配时间
                    saleChance.setAssignTime(new Date());
                }
            }
        }
        //执行更新操作
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "更新失败，请重试！");
    }


    /**
     * 删除营销机会数据
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        AssertUtil.isTrue(ids == null || ids.length == 0, "请选择要删除的记录！");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length, "营销机会数据删除失败！");
    }

    //更新开发状态
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDevResult(Integer id,Integer devResult){
        //验证营销机会是否存在
        AssertUtil.isTrue(null==id ,"营销机会不存在，请重试");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(saleChance==null,"营销机会不存在，请重试");

        //修改状态
        saleChance.setDevResult(devResult);
        //执行修改操作
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"开发状态修改失败");



    }
}
