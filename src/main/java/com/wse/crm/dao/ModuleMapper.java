package com.wse.crm.dao;

import com.wse.crm.base.BaseMapper;
import com.wse.crm.model.TreeModel;
import com.wse.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    // 按照指定的数据格式，查询所有的资源列表
    List<TreeModel> queryAllModules();

    //查询所有的资源，返回Module对象
    List<Module> queryModules();

    //同模块层级码和模块名获取模块对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade,@Param("moduleName") String moduleName);

    //通过层级码和下级菜单的url获取模块对象
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade,@Param("url") String url);

    //通过权限码获取模块对象
    Module queryModuleByOptValue(String optValue);

    // 查询菜单是否存在子菜单
    Integer countSubModuleByParentId(Integer mId);

    // 删除资源
    int deleteModule(Integer mId);
}