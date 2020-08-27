layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 添加或更新计划项
     */
    form.on("submit(addOrUpdateCusDevPlan)", function (data) {
        // 弹出loading层
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});

        // 非空判断 TODO

        // 发送ajax请求

        // 请求路径
        var url = ctx + "/cus_dev_plan/add"; // 添加操作
        // 判断隐藏域中主键的值是否存在
        var cusDevPlanId = $("[name='id']").val();
        // 判断是否为空
        if(cusDevPlanId != null && cusDevPlanId != "") {
            url = ctx + "/cus_dev_plan/update"; // 修改操作
        }

        // 请求参数
        var params = data.field;

        $.ajax({
            type:"post",
            url:url,
            data:params,
            success:function (result) {
                if (result.code == 200) {
                    // 关闭弹出层（返回值为index的弹出层）
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    // 关闭所有ifream层
                    layer.closeAll("iframe");
                    // 刷新父页面
                    parent.location.reload();
                } else {
                    layer.msg(result.msg, {icon:5});
                }
            }
        });
        return false;
    });

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

});