package com.wse.crm.service;

import com.wse.crm.base.BaseService;
import com.wse.crm.dao.ModuleMapper;
import com.wse.crm.dao.PermissionMapper;
import com.wse.crm.model.TreeModel;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 20:12
 */
@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 按照指定的数据格式，查询所有的资源列表
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId){

        // 所有资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();

        // 查询指定角色拥有的资源列表
        List<Integer> roleHasPermissionIds =  permissionMapper.queryPermissionByRole(roleId);

        // 判断角色ID是否为空
        if (roleId != null) {
            // 判断当前资源是否被选中
            for (TreeModel tree:treeModelList) {
                // 判断当前角色是否包含该资源
                if (roleHasPermissionIds.contains(tree.getId())) {
                    tree.setChecked(true);
                }
            }
        }

        return treeModelList;
    }


    /**
     * 查询所有的资源列表
     * @return
     */
    public Map<String,Object> moduleList(){
        //返回给页面的标准格式，必须是Map<String,Object>
        Map<String,Object> result = new HashMap<String,Object>();
        //查询所有的资源，返回Module对象集合
        List<Module> modules =moduleMapper.queryModules();
        //设置返回的属性
        result.put("count",modules.size());
        result.put("data",modules);
        result.put("code",0);
        result.put("msg","");
        return result;
    }


    /**
     * 添加
     * 1.参数校验
     *      模块名-module_name
     *          ⾮空  同⼀层级下模块名唯⼀
     *      url
     *          ⼆级菜单    ⾮空  不可重复
     *      上级菜单-parent_id
     *          ⼀级菜单    null
     *          ⼆级|三级菜单     parent_id ⾮空必须存在
     *      层级-grade
     *          ⾮空  0|1|2
     *      权限码 optValue
     *          ⾮空不可重复
     * 2.参数默认值设置
     *      is_valid create_date update_date
     * 3.执⾏添加判断结果
     *
     * @param module    模块对象
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module){
        //moduleName验证非空且同一层级下不可重复
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入菜单名");
        //获取菜单层级
        Integer grade = module.getGrade();
        //grade层级非空，且只能为0,1,2
        AssertUtil.isTrue(grade==null || !(grade==0||grade==1||grade==2),"菜单层级不合法");
        //通过菜单名和层级获取菜单module，不可重复
        //通过层级和模块名获取模块对象
        Module temp = moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(), module.getModuleName());
        AssertUtil.isTrue(temp != null && !(module.getId().equals(temp.getId())),"该层级菜单名重复");

        //当层级为1是，需指定二级菜单的url，及父级菜单的id
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单url值");
            AssertUtil.isTrue(null
                            !=moduleMapper.queryModuleByGradeAndUrl(module.getGrade(),module.getUrl()),"二级菜单url 不可重复!");
        }


        //  parentId父级菜单  如果是一级菜单，值为-1  如果是二级或三级菜单，值非空，且父菜单存在
        if (module.getGrade() == 0) {
            module.setParentId(-1);
        } else {
            AssertUtil.isTrue(module.getParentId() == null, "父级菜单不能为空！");
            // 通过parentId查询资源对象
            temp = moduleMapper.selectByPrimaryKey(module.getParentId());
            // 判断 父级菜单是否存在
            AssertUtil.isTrue(temp == null , "父级菜单必须存在！");
        }

        // 权限码  非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空！");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        AssertUtil.isTrue(temp != null, "权限码已存在，请重试！");


        /* 设置默认值 */
        module.setIsValid((byte) 1);
        module.setUpdateDate(new Date());
        module.setCreateDate(new Date());


        /* 添加操作 */
        AssertUtil.isTrue(moduleMapper.insertSelective(module) != 1, "资源数据添加失败！");
    }


    /**
     * 修改模块的功能
     *      1. 非空校验
     *          id
 *                 不为空，且数据存在
 *              grade层级
 *                 非空，取值为 0|1|2
 *              moduleName模块名
 *                  非空，在同一层级下名称唯一
 *              url菜单地址
 *                 如果是二级菜单（grade=1），非空且唯一
 *              parentId父级菜单
 *                 如果是一级菜单，值为-1
 *                 如果是二级或三级菜单，值非空，且父菜单存在
 *              权限码
 *                 非空且唯一
     *          2. 设置默认值
     *             updateDate
     *          3. 更新操作
     *
     * @param module    模块对象
     */
    public void updateModule(Module module){

        //获取模块id，并判断模块是否存在，不存在抛出异常
        AssertUtil.isTrue(null==module.getId()||moduleMapper.selectByPrimaryKey(module.getId())==null,"待更新模块不存在");

        //判断层级非空且只能为0,1,2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null==grade||!(grade==0||grade==1||grade==2),"菜单层级不合法");

        //判断模块名不能为空
        AssertUtil.isTrue(null==module.getModuleName(),"模块名不能为空");

        //判断同一层级下模块名不能重复
        Module temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        AssertUtil.isTrue(null==temp,"该层级下菜单名已存在");

        //如果是二级菜单（grade=1），url菜单地址非空且唯一
        if (grade==1){
            AssertUtil.isTrue(null==module.getUrl(),"请指定下级菜单url");
             temp = moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            AssertUtil.isTrue(null==temp,"该下级菜单已存在");
        }
        //  parentId父级菜单  如果是一级菜单，值为-1  如果是二级或三级菜单，值非空，且父菜单存在
        if (module.getGrade() == 0) {
            module.setParentId(-1);
        } else {
            AssertUtil.isTrue(module.getParentId() == null, "父级菜单不能为空！");
            // 通过parentId查询资源对象
            temp = moduleMapper.selectByPrimaryKey(module.getParentId());
            // 判断 父级菜单是否存在
            AssertUtil.isTrue(temp == null , "父级菜单必须存在！");
        }

        // 权限码  非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空！");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        AssertUtil.isTrue(temp != null && !(module.getId().equals(temp.getId())), "权限码已存在，请重试！");

        // 设置默认值
        module.setUpdateDate(new Date());

        // 更新操作
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) != 1, "资源数据更新失败！");

    }


    /**
     * 删除资源
     * @param mId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer mId) {
        // 判断模块是否存在（不存在则抛出异常）
        AssertUtil.isTrue(null==moduleMapper.selectByPrimaryKey(mId),"待删除模块不存在");

        // 将mId作为父Id去查询子菜单的数量，判断是否存在子菜单（存在则抛出异常）
        Integer count = moduleMapper.countSubModuleByParentId(mId);
        // 判断子菜单数量是否大于0，（大于0，存在子菜单，不可删除，抛出异常）
        AssertUtil.isTrue(count >0, "存在子菜单，不可删除！");


        // 查询权限表是否有id为mId的数据，是否存在中间表的数据
        count = permissionMapper.queryPermissionByModuleId(mId);
        if (count > 0) {
            // 删除中间表的数据
            AssertUtil.isTrue(permissionMapper.deleteByModuleId(mId) != count, "菜单删除失败！");
        }

        // 删除操作
        AssertUtil.isTrue(moduleMapper.deleteModule(mId) != 1, "菜单删除失败！");
    }
}
