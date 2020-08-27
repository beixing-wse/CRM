$(function () {
    loadModuleInfo();
});


var zTreeObj;
/**
 * 加载资源数据
 */
function loadModuleInfo() {
    // 发送ajax请求
    $.ajax({
        type:"get",
        url:ctx +"/module/queryAllModules?roleId=" + $("[name='roleId']").val(),
        data:{},
        success:function (data) {
            // console.log(data);

            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback:{
                    // 用于捕获 checkbox / radio 被勾选 或 取消勾选的事件回调函数
                    onCheck: zTreeOnCheck // 绑定具体的函数名
                }
            };

            zTreeObj = $.fn.zTree.init($("#test1"), setting, data);

        }
    });

    /**
     * 用来监听复选框 选中或取消选中
     */
    function zTreeOnCheck() {
        // 获取复选框被选中的节点集合
        var nodes = zTreeObj.getCheckedNodes(true);
        // console.log(nodes);

        // mIds=1&mIds=2&mIds=3
        var mIds = "mIds=";
        // 判断 节点集合是否为空
        if (nodes != null && nodes.length) {
            for(var i = 0; i < nodes.length; i++) {
                if (i < nodes.length - 1) {
                    mIds += nodes[i].id + "&mIds=";
                } else {
                    mIds += nodes[i].id;
                }
            }
        }
        //console.log(mIds);
        // 角色ID
        var roleId = $("[name='roleId']").val();

        // 发送ajax请求，执行授权操作
        $.ajax({
            type:"post",
            url:ctx + "/role/addGrant",
            data:mIds+"&roleId="+roleId,
            success:function (data) {
                if (data.code != 200) {
                    layer.msg(data.msg,{icon:5});
                }
            }
        });


    }
}

