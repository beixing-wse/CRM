layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 监听表单的submit事件
     */
    form.on('submit(addOrUpdateSaleChance)',function (obj) {

        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });

        // 添加操作
        var url = ctx + "/sale_chance/add";

        // 获取隐藏域中的值
        var saleChanceId = $("[name='id']").val();
        // 如果id不为空，则为修改操作
        if (saleChanceId != null && saleChanceId != "") {
            // 修改操作
            url = ctx + "/sale_chance/update";
        }


        console.log(obj.field)
        // 发送ajax请求
        $.ajax({
            type:"post",
            url: url,
            data:obj.field,
            success:function (result) {
                // 关闭加载层
                layer.close(index);
                if (result.code == 200) {
                    layer.msg("操作成功！",{icon:6});
                    // 关闭添加/修改弹出层
                    layer.closeAll("iframe");
                    // 刷新父页面，重新渲染表格数据
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
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });


    /**
     * 加载下拉框（指派人）
     */
    $.get(ctx + "/user/queryAllSales",function (data) {
        console.log(data);
        // 判断数据是否为空
        if (data != null && data.length >0) {
            for(var i = 0; i < data.length; i++) {

                var opt ="";

                // 获取修改前的指派人
                var assignManId = $("[name='assignManId']").val();
                // 判断当前循环到的指派人ID是否与修改数据的指派人id一致，如果一致则选中
                if (data[i].id == assignManId) {
                    opt = "<option selected value='"+data[i].id+"'>"+data[i].userName+"</option>";
                } else {
                    opt = "<option value='"+data[i].id+"'>"+data[i].userName+"</option>";
                }

                // 将下拉选项设置到指定下拉框
                $("#assignMan").append(opt);
            }
        }
        // 重新渲染下拉框内容
        layui.form.render("select");
    });


});