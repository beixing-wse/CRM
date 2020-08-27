layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        // page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });

    /**
     * 头部工具栏监听事件
     */
    table.on('toolbar(cusDevPlans)', function (obj) {
        // 获取营销机会的ID （隐藏域中）
        var sId = $("[name='id']").val();

        if (obj.event == "add") {
            // 添加计划项
            openAddOrUpdateCusDevPlanDialog();
        } else if (obj.event == "success") {
            // 修改开发状态为开发成功
            updateSaleChanceDevResult(2,sId);
        } else if (obj.event == "failed") {
            // 修改开发状态为开发失败
            updateSaleChanceDevResult(3,sId);
        }
    });


    /**
     * 修改营销机会的开发状态
     * @param devResult
     * @param sId
     */
    function  updateSaleChanceDevResult(devResult, sId) {
        layer.confirm("确认执行当前操作？", {icon:3, title:"计划项维护"}, function (index) {
            // 关闭确认框
            layer.close(index);

            // 发送ajax请求
            $.post(ctx + "/sale_chance/updateDevResult",{id:sId,devResult:devResult},function (result) {
                if (result.code == 200) {
                    layer.msg("更新成功！", {icon:6});
                    // 关闭iframe层
                    layer.closeAll("iframe");
                    // 刷新窗口
                    parent.location.reload();
                } else {
                    layer.msg(result.msg, {icon:5});
                }
            });

        });
    }



    /**
     * 行工具栏监听事件
     */
    table.on('tool(cusDevPlans)', function (obj) {
        if (obj.event == "edit") {
            // 更新计划项
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        } else if (obj.event == "del") {
            // 删除开发项
            // 询问是否确认删除
            layer.confirm("确定要删除这条记录吗？", {icon: 3, title:"客户开发计划管理"}, function (index) {
                // 关闭窗口
                layer.close(index);
                // 发送ajax请求，删除记录
                $.ajax({
                    type:"post",
                    url:ctx + "/cus_dev_plan/delete",
                    data:{
                        id:obj.data.id
                    },
                    success:function (data) {
                        if (data.code == 200){
                            layer.msg("删除成功！",{icon:6});
                            // 刷新表格
                            tableIns.reload();
                        } else {
                            layer.msg(data.msg, {icon:5});
                        }
                    }
                });

            });
        }
    });

    /**
     * 打开 添加/更新 j客户开发计划的弹出层
     * @param cId
     */
    function openAddOrUpdateCusDevPlanDialog(cId) {
        var title = "计划项管理 - 添加计划项";
        var url = ctx + "/cus_dev_plan/toAddOrUpdateCusDevPlanPage";

        // 得到营销机会的ID （隐藏域中）
        var saleChanceId = $("[name='id']").val();
        url += "?saleChanceId=" + saleChanceId;

        // 如果cId不为空，则为修改操作
        if (cId != null && cId != "") {
            title = "计划项管理 - 更新计划项";
            url += "&cId=" + cId;
        }

        layui.layer.open({
            title:title,
            type: 2,
            area:["500px","300px"],
            maxmin: true,
            content:url
        });
    }




});
