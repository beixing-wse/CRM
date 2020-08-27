package com.wse.crm.controller;

import com.wse.crm.base.BaseController;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.query.RoleQuery;
import com.wse.crm.service.RoleService;
import com.wse.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 10:23
 */
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;
    /**
     * 查询⻆⾊列表
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }


    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    /**
     * 查询所有角色
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> userList(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }


    /**
     * 跳转到角色添加或更新页面
     * @param id        角色id
     * @param request   请求
     * @return
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer id, HttpServletRequest request){
        if(null !=id){
            request.setAttribute("role",roleService.selectByPrimaryKey(id));
        }
        return "role/add_update";
    }


    /**
     * 添加角色记录的方法
     * @param role      角色
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.saveRole(role);
        return success("⻆⾊记录添加成功");
    }

    /**
     * 更新角色记录的方法
     * @param role      角色
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("⻆⾊记录更新成功");
    }


    /**
     * 删除角色记录的方法
     * @param roleId    角色id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleId){
        roleService.deleteRole(roleId);
        return success("⻆⾊记录删除成功");
    }

    /**
     * 进入授权页面
     * @param roleId
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, HttpServletRequest request) {
        // 将角色ID设置到请求域中，页面中可以得知当前是给什么角色授权
        request.setAttribute("roleId", roleId);

        return "role/grant";
    }

    /**
     * 添加用户权限
     * @param mIds
     * @param roleId
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mIds,Integer roleId){
        roleService.addGrant(mIds,roleId);
        return success("权限添加成功");
    }


}
