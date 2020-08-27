layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;



    /**
     * 表单submit绑定
     */
    form.on('submit(addOrUpdateRole)', function (obj) {

        var url = ctx + "/role/add";

        // 获取隐藏域中的用户ID
        var roleId = $("[name='id']").val();
        if (roleId != null && roleId != "") {
            url = ctx + "/role/update";
        }

        $.ajax({
            type:"post",
            url:url,
            data:obj.field,
            success:function (data) {
                if (data.code == 200) {
                    layer.msg("操作成功！",{icon:6});
                    // 关闭对话框
                    layer.closeAll("iframe");
                    // 刷新
                    parent.location.reload();
                } else {
                    layer.msg(data.msg, {icon:5})
                }
            }
        });

        return false; // 阻止表单提交
    });




});