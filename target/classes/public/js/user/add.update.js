layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    // 引⼊formSelects 模块
    formSelects = layui.formSelects;

    /**
     * 添加或更新⽤户
     */
    form.on("submit(addOrUpdateUser)", function (data) {
        // 弹出loading层
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade:
                0.8});
        var url = ctx + "/user/save";
        if($("input[name='id']").val()){
            url = ctx + "/user/update";
        }
        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    // 关闭弹出层（返回值为index的弹出层）
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    // 关闭所有ifream层
                    layer.closeAll("iframe");
                    // 刷新⽗⻚⾯
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        });
        return false;
    });
    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执⾏关闭
    });


    /**
     * 加载角色下拉框
     *  formSelects.config(参数一,参数二):
     *      参数一：需要绑定的元素的xm-select属性值
     *      参数二：配置项
     *          type：请求类型  GET|POSt等
     *          searchUrl：请求的路径
     *          keyName：返回的数据对应的key值 （显示给用户看的）
     *          keyVal：返回的数据对应的value值 （传递到后台的）
     *       参数三：isJson，是否传输json数据, true将添加请求头 Content-Type: application/json; charset=UTF-8
     */
    formSelects.config("selectId",{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?userId=" + $("[name='id']").val(),
        keyName: "roleName",
        keyVal: "id"
    },true);


});
