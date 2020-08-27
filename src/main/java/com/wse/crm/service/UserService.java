package com.wse.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wse.crm.base.BaseMapper;
import com.wse.crm.base.BaseService;
import com.wse.crm.base.ResultInfo;
import com.wse.crm.dao.UserMapper;
import com.wse.crm.dao.UserRoleMapper;
import com.wse.crm.model.UserModel;
import com.wse.crm.query.UserQuery;
import com.wse.crm.util.AssertUtil;
import com.wse.crm.util.Md5Util;
import com.wse.crm.util.PhoneUtil;
import com.wse.crm.util.UserIDBase64;
import com.wse.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wse
 * @version 1.0
 * @date 2020/8/19 0019 12:26
 */
@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserRoleMapper userRoleMapper;


    /**
     * 用户登录
     *      1. 参数校验 （非空校验）
     *           判断用户名与是密码是否为空
     *      2、判断用户是否存在
     *           通过用户名查询获取数据库用户对象
     *           判断是否存在
     *      3、判断用户密码是否正确
     *              不正确，结束方法
     *              正确登入成功
     * @param userName      用户名
     * @param userPwd       用户密码
     */
    public UserModel userLogin(String userName, String userPwd) {
        //1、用户名和密码不为空的参数校验
        checkLoginParams(userName,userPwd);

        //通过用户名查询用户
        User user = userMapper.queryUserByName(userName);
        //2、判断用户是否存在，不存在则抛出异常
        AssertUtil.isTrue(null == user, "用户不存在，请重试");

        //3、比较数据库用户密码与userPwd是否相同
        checkUserPwd(user.getUserPwd(),userPwd);

        //封装UserModel对象，密码正确（⽤户登录成功，返回⽤户的相关信息）
        UserModel userModel = buildUserModel(user);

        return userModel;

    }

    /**
     * 用户名和密码不为空的参数校验
     * @param userName      用户名
     * @param userPwd       密码
     */
    public void checkLoginParams(String userName,String userPwd){
        //参数校验（用户名），用户名为空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //参数校验（密码），用户密码为空则抛出异常
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空");
    }

    /**
     *比较数据库用户密码与userPwd是否相同的方法
     *
     * @param pwd       数据库中的用户密码
     * @param userPwd   用户输入密码
     */
    public void checkUserPwd(String pwd,String userPwd){
        //将用户输入的密码进行加密
        userPwd = Md5Util.encode(userPwd);
        //用户密码不正确抛出异常
        AssertUtil.isTrue(!pwd.equals(userPwd), "用户密码不正确");

    }

    /**
     * 构建返回的用户信息
     * @param user
     * @return
     */
    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();

        // 设置⽤户信息（将userId 加密）
        userModel.setUserId(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;

    }




    /**
     * 客户修改密码
     *
     * 1. 参数校验
     *      ⽤户ID：userId ⾮空⽤户对象必须存在
     *      原始密码：oldPassword ⾮空与数据库中密⽂密码保持⼀致
     *      新密码：newPassword ⾮空与原始密码不能相同
     *      确认密码：confirmPassword ⾮空与新密码保持⼀致
     * 2. 设置⽤户新密码
     *      新密码进⾏加密处理
     * 3. 执⾏更新操作
     *      受影响的⾏数⼩于1，则表示修改失败
     *
     *      注：在对应的更新⽅法上，添加事务控制
     * @param userId        用户id
     * @param oldPwd        原始密码
     * @param newPwd        新的密码
     * @param confirmPwd    确认密码
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPwd(Integer userId, String oldPwd, String newPwd, String confirmPwd) {
        //通过userId获取对象
        User user = userMapper.selectByPrimaryKey(userId);

        //1、参数校验
        checkPwdParams(user, oldPwd, newPwd, confirmPwd);
        //修改密码
        AssertUtil.isTrue(userMapper.updateUserPwd(userId, Md5Util.encode(newPwd)) < 1, "修改密码失败");

    }

    /**
     * 校验用户密码参数
     *      ⽤户ID：userId ⾮空⽤户对象必须存在
     *      原始密码：oldPassword ⾮空与数据库中密⽂密码保持⼀致
     *      新密码：newPassword ⾮空与原始密码不能相同
     *      确认密码：confirmPassword ⾮空与新密码保持⼀致
     * @param user          用户对象
     * @param oldPwd        原始密码
     * @param newPwd        新的密码
     * @param confirmPwd    确认密码
     */
    private void checkPwdParams(User user, String oldPwd, String newPwd, String confirmPwd) {
        //1.判断用户是否存在
        AssertUtil.isTrue(null == user, "用户未登入或不存在！");
        //2.判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空！");
        //3.判断原始密码是否与数据库用户对象密码相同（加密后再比较）
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPwd))), "原始密码不正确！");
        //4.判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空！");
        //5.判断新密码是否与原始面相同
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码与原始密码不一致！");
        //6.判断确认密码是否新密码与相同
        AssertUtil.isTrue(!confirmPwd.equals(newPwd), "确认密码与新密码不一致！");

    }



    /**
     * 查询所有的销售人员
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {

        return userMapper.queryAllSales();
    }

     /**
     * 添加⽤户
     *      1. 参数校验
     *          ⽤户名⾮空唯⼀性
     *          邮箱⾮空
     *          ⼿机号⾮空格式合法
     *      2. 设置默认参数
     *          isValid 1
     *          creteDate 当前时间
     *          updateDate 当前时间
     *          userPwd 123456 -> md5加密
     *      3. 执⾏添加，判断结果
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        // 1. 参数校验
        checkParams(user);
        // 2. 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        // 3. 执⾏添加，判断结果
        //AssertUtil.isTrue(userMapper.insertSelective(user) != 1, "⽤户添加失败！");

        // 会返回主键，主键会自动设置到user对象中
        AssertUtil.isTrue(userMapper.insertHasKey(user) != 1, "用户添加失败！");

        System.out.println(user.getId());


        // 添加用户角色关联数据
        userRoleService.relaionUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 参数校验
     * @param user
     */
    private void checkParams(User user) {
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "⽤户名不能为空！");
        // 验证⽤户名是否存在
        User temp = userMapper.queryUserByName(user.getUserName());
        // 判断用户Id是否为空，不为空则为修改操作
        if (user.getId() == null) {
            // 如果用户对象存在，则不可用
            AssertUtil.isTrue(temp != null, "用户名已存在，请重新输入！");
        } else {
            // 如果用户对象存在，则不可用
            AssertUtil.isTrue(temp != null && !(user.getId().equals(temp.getId())), "用户名已存在，请重新输入！");
        }
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()), "请输⼊邮箱地址！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()), "⼿机号码格式不正确！");
    }

    /**
     * 多条件分页查询用户数据
     * @param query
     * @return
     */
    public Map<String,Object> selectUserByParams(UserQuery query){
        //创建map对象,返回给前台的固定使用Map<String,Object>
        Map<String, Object> map = new HashMap<>();

        //设置页码和每页显示个数
        PageHelper.startPage(query.getPage(),query.getLimit());

        PageInfo<User> userPageInfo = new PageInfo<>(userMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","");
        map.put("count",userPageInfo.getSize());
        map.put("data",userPageInfo.getList());
        return map;

    }

    /**
     * 更新⽤户
     *
     *      1. 参数校验
     *          id ⾮空记录必须存在
     *          ⽤户名⾮空唯⼀性
     *          email ⾮空
     *          ⼿机号⾮空格式合法
     *      2. 设置默认参数
     *             updateDate
     *      3. 执⾏更新，判断结果

     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {

        // 1. 参数校验
        AssertUtil.isTrue(null==user.getId(),"网络异常，请重试");
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkParams(user);
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "⽤户更新失败！");

        // 添加用户角色关联数据
        userRoleService.relaionUserRole(user.getId(), user.getRoleIds());
    }


    /**
     * 删除⽤户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null==ids || ids.length == 0,"请选择待删除的⽤户记录!");

        // 删除用户角色关联信息
        for (Integer userId : ids) {
            // 通过用户Id查询对应的用户角色关联信息
            Integer count =  userRoleMapper.selectUserRoleCountByUserId(userId);
            // 判断是否存在当前用户的用户关联信息
            if (count > 0) {
                // 如果存在，则删除原有的关联数据
                AssertUtil.isTrue(userRoleMapper.deleteRelationByUserId(userId) != count,"用户角色关联失败！");
            }
        }

        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length,"⽤户记录删除失败!");
    }




}
