layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    /**
     * 表单提交
     */
    form.on('submit(saveBtn)', function (data) {

        // 得到表单元素的值
        var field = data.field;

        // 发送ajx请求
        $.ajax({
            type:"post",
            url:ctx + "/user/updateUserPwd",
            data:{
                oldPwd:field.old_password,
                newPwd:field.new_password,
                confirmPwd:field.again_password
            },
            success:function (data) {
                // 判断是否更新成功
                if (data.code == 200) {
                    // 修改成功后，用户自动退出系统
                    layer.msg("用户密码修改成功，系统将在3秒钟后退出...",{icon:6}, function () {
                        // 退出系统后，删除对应的cookie
                        $.removeCookie("userIdStr", {domain:"localhost",path:"/crm"});
                        $.removeCookie("userName", {domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName", {domain:"localhost",path:"/crm"});

                        // 跳转到登录页面 (父窗口跳转)
                        window.parent.location.href = ctx + "/index";

                    });
                } else {
                    layer.msg(data.msg,{icon:5})
                }
            }
        });

        return false; // 阻止表单提交
    });


});