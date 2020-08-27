package com.wse.crm.controller;

import com.wse.crm.base.BaseController;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.model.TreeModel;
import com.wse.crm.service.ModuleService;
import com.wse.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 20:15
 */
@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Resource
    private ModuleService moduleService;

    /**
     * 通过角色id查询所有模块对象
     * @return
     */
    @ResponseBody
    @RequestMapping("queryAllModules")
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    /**
     *进入模块管理页面
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "module/module";
    }

    /**
     * 展示所有的模块的方法
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> moduleList(){
        return moduleService.moduleList();
    }

    // 添加资源⻚视图转发

    /**
     *打开添加资源的页面
     *
     * @param grade     模块层级
     * @param parentId  模块父id
     * @param model     模块对象
     * @return
     */
    @RequestMapping("addModulePage")
    public String addModulePage(Integer grade, Integer parentId, Model model){
        model.addAttribute("grade",grade);
        model.addAttribute("parentId",parentId);
        return "module/add";
    }

    /**
     * 添加模块对象的方法
     *
     * @param module
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("菜单添加成功");
    }

    /**
     * 更新模块对象的方法
     *
     * @param module    模块对象
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("菜单更新成功");
    }


    /**
     * 打开更新资源的页面
     * @param id        模块id
     * @param request   请求
     * @return
     */
    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id, HttpServletRequest request) {

        // 通过id查询资源对象
        Module module = moduleService.selectByPrimaryKey(id);

        request.setAttribute("module", module);

        return "module/update";
    }

    /**
     * 删除资源
     * @param mId
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer mId){

        moduleService.deleteModule(mId);

        return success();
    }



}
