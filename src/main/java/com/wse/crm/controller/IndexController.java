package com.wse.crm.controller;

import com.wse.crm.base.BaseController;
import com.wse.crm.dao.PermissionMapper;
import com.wse.crm.service.PermissionService;
import com.wse.crm.service.UserService;
import com.wse.crm.util.LoginUserUtil;
import com.wse.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;

    /**
     * 系统登录页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }


    // 系统界面欢迎页
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

    /**
     * 后端管理主页面
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        // 从cookie中获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 获取用户对象
        User user = userService.selectByPrimaryKey(userId);
        // 将用户对象设置到session作用域中
        request.getSession().setAttribute("user",user);

         //用户登录成功后，查询用户拥有的所有权限 （权限码）
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
//        // 将权限集合设置到session
        request.getSession().setAttribute("permissions",permissions);

        return "main";
    }

}
