package com.wse.crm.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wse.crm.base.BaseController;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.exceptions.ParamsException;
import com.wse.crm.model.UserModel;
import com.wse.crm.query.UserQuery;
import com.wse.crm.service.UserService;
import com.wse.crm.util.LoginUserUtil;
import com.wse.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/19 0019 12:34
 */
@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    /**
     * 用户登录
            1. 通过形参接收请求参数
            2. 调用Service层的方法，得到登录结果 （通过try catch捕获service层的异常）
            3. 响应结果给客户端 （返回resultInfo）
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();

            //调用service方法登入
            UserModel userModel = userService.userLogin(userName, userPwd);
            // 将封装的数据设置到resultInfo中响应给客户端
            resultInfo.setResult(userModel);


        return resultInfo;
    }


    /**
     * 修改用户密码
     *      1、从cookie中获取用户id
     *      2、调用service的修改方法
     *      3、返回结果对象
     * @param oldPwd        原始密码
     * @param newPwd        新的密码
     * @param confirmPwd    确认密码
     * @param request       请求
     * @return
     */
    @RequestMapping("/updateUserPwd")
    @ResponseBody
    public ResultInfo updateUserPwd(String oldPwd, String newPwd, String confirmPwd, HttpServletRequest request){

        ResultInfo resultInfo = new ResultInfo();
        // 从cookie中获取当前登录用户的ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);

        //调用service的修改密码的方法
        userService.updateUserPwd(userId,oldPwd,newPwd,confirmPwd);

        return resultInfo;
    }


    /**
     * 跳转用户修改密码页面
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toUpdateUserPwdPage() {
        return "user/password";
    }

    /**
     * 查询所有的销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }


    /**
     * 进⼊⽤户管理⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 多条件分页查询用户数据
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selectUserByParams(UserQuery query){
        return userService.selectUserByParams(query);

    }




    /**
     * 添加⽤户
     * @param user
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("⽤户添加成功！");
    }

    /**
     * 更新⽤户
     * @param user
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("⽤户更新成功！");
    }

    /**
     * 进⼊⽤户添加或更新⻚⾯
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model) {
        if (null != id) {
            model.addAttribute("userInfo", userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }

    /**
     * 删除⽤户
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteBatch(ids);
        return success("⽤户记录删除成功");
    }


}
