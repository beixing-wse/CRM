package com.wse.crm.service;

import com.wse.crm.base.BaseService;
import com.wse.crm.dao.ModuleMapper;
import com.wse.crm.dao.PermissionMapper;
import com.wse.crm.dao.RoleMapper;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.vo.Module;
import com.wse.crm.vo.Permission;
import com.wse.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/24 0024 10:21
 */
@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;
    /**
     * 查询⻆⾊列表
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }


    /**
     * 添加用户角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输⼊⻆⾊名!");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp,"该⻆⾊已存在!");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(role)<1,"⻆⾊记录添加失败!");
    }

    /**
     * 更新用户角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        AssertUtil.isTrue(null==role.getId()||null==selectByPrimaryKey(role.getId()),"待修改的记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输⼊⻆⾊名!");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(role.getId())),"该⻆⾊已存在!");
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"⻆⾊记录更新失败!");
    }

    /**
     * 删除用户角色
     * @param roleId      角色id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        Role role = roleMapper.selectByPrimaryKey(roleId);

        AssertUtil.isTrue(null==role,"待删除用户不存在");
        role.setIsValid(0);
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"用户删除失败");
    }


    /**
     * 添加用户权限
     *      1、判断角色是否存在
     *      2、判断角色是否拥有权限（有则删除）
     *      3、得到新的权限，给角色批量授予权限
     * @param mIds
     * @param roleId
     */
    public void addGrant(Integer[] mIds, Integer roleId) {
        /**
         * 核⼼表-t_permission t_role(校验⻆⾊存在)
         * 如果⻆⾊存在原始权限删除⻆⾊原始权限
         * 然后添加⻆⾊新的权限批量添加权限记录到t_permission
         */
        //通过roleId查询角色，判断角色是否存在
        Role temp =selectByPrimaryKey(roleId);
        //判断角色是否存在，不存在则抛出异常
        AssertUtil.isTrue(null==roleId||null==temp,"待授权的⻆⾊不存在!");

        //通过roleId查询角色所拥有的权限个数
        int count = permissionMapper.queryPermissionCountByRoleId(roleId);
        //如果有权限，则删除
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deleteByRoleId(roleId)
                    <count,"权限分配失败!");
        }

        // 添加角色的权限
        // 判断模块ID是否存在
        if(null !=mIds && mIds.length>0){
            //定义权限集合
            List<Permission> permissions = new ArrayList<Permission>();
            //循环遍历前台传来的权限id集合添加到定义的集合中去
            for (Integer mId : mIds) {
                Permission permission=new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mId);
                permission.setRoleId(roleId);
                // 通过模块ID得到资源对象
                Module module = moduleMapper.selectByPrimaryKey(mId);
                permission.setAclValue(module.getOptValue());
                permissions.add(permission);
            }
            // 批量添加
            AssertUtil.isTrue(permissionMapper.insertBatch(permissions) != permissions.size(), "角色授权失败！");
        }
    }


}
