layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 营销机会列表展示
     */
    var  tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url : ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分页
        height : "full-125",
        limits : [10,15,20,25], // 分页时可选的每页显示的数量
        limit : 10, // 默认每页显示10条
        toolbar: "#toolbarDemo", // 绑定头部工具栏
        id : "saleChanceListTable", // 数据表格的唯一标识
        cols : [[
            // type 单元格类型；checkbox表示显示的是复选框
            {type: "checkbox", fixed:"center"},
            // field属性的值要与JavaBean对象中的属性字段名一致
            // title属性相当于表头
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'uname', title: '指派人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            // templet:可以将数据进行对应的格式化，再返回格式化后的结果
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    /**
     * 格式化分配状态
     *  0 - 未分配
     *  1 - 已分配
     *  其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if(state==1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     *  0 - 未开发
     *  1 - 开发中
     *  2 - 开发成功
     *  3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value){
        if(value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if(value==1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if(value==2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if(value==3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }

    }


    /**
     * 多条件搜索
     *      点击搜索按钮时，绑定搜索按钮的点击事件,表格重载进行条件查询
     */
    $(".search_btn").click(function () {
        // 表格重载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                /* 传递给营销机会查询的条件 */
                // 客户名
                customerName:$("[name='customerName']").val(),
                // 创建人
                createMan:$("[name='createMan']").val(),
                // 分配状态
                state:$("#state").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 监听头部工具栏
     table.on('toolbar(数据表格的lay-filter属性值)', function (obj) {

        });
     */
    table.on('toolbar(saleChances)', function (obj) {
        // console.log(obj);
        // 判断事件类型
        switch (obj.event) {
            case "add":
                // 添加操作
                openAddOrUpdateSaleChanceDialog();
                break;
            case "del":
                // 删除操作
                var checkStatus = table.checkStatus(obj.config.id);
                console.log(checkStatus);
                deleteSaleChance(checkStatus);

        }
    });


    /**
     * 删除营销机会
     * @param data
     */
    function deleteSaleChance(data) {
        // 得到所有被选中的记录
        var result = data.data;
        // 判断用户是否选择额记录
        if (result.length == 0) {
            layer.msg("请选择要删除的记录",{icon:5});
            return;
        }


        layer.confirm("您确定要删除选中的记录吗？",{
            btn:["确认","取消"],
        },function (index) {
            // 关闭确认框
            layer.close(index);

            // 得到要删除的数据的ID  ids=1&ids=2&ids=3
            var ids = "ids=";
            for(var i = 0; i < result.length; i++) {
                // 如果遍历到最后一个，则不再拼接
                if (i < result.length -1) {
                    ids += result[i].id + "&ids=";
                } else {
                    ids += result[i].id;
                }
            }

            // console.log(ids);

            // 发送ajax请求
            $.post(ctx + "/sale_chance/delete",ids,function (data) {
                if (data.code == 200){
                    layer.msg("删除成功！",{icon:6});
                    // 刷新表格
                    tableIns.reload();
                } else {
                    layer.msg(data.msg, {icon:5});
                }
            });


        });

    }

    /**
     * 绑定行工具栏
     table.on('tool(数据表格的lay-filter属性值)', function (obj) {

        });
     */
    table.on('tool(saleChances)', function (obj) {
        // console.log(obj);
        if (obj.event == "edit") {
            // 编辑操作
            openAddOrUpdateSaleChanceDialog(obj.data.id);
        } else if (obj.event == "del") {
            // 删除操作
            // 询问是否确认删除
            layer.confirm("确定要删除这条记录吗？", {icon: 3, title:"营销机会数据管理"}, function (index) {
                // 关闭窗口
                layer.close(index);
                // 发送ajax请求，删除记录
                $.ajax({
                    type:"post",
                    url:ctx + "/sale_chance/delete",
                    data:{
                        ids:obj.data.id
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
     * 打开营销机会添加/修改的对话框
     */
    function openAddOrUpdateSaleChanceDialog(saleChanceId) {

        // 标题
        var title = "<h2>营销机会管理 - 添加机会</h2>";
        // url
        var url = ctx + "/sale_chance/toSaleChancePage";

        // 判断saleChanceId是否为空
        if (saleChanceId != null && saleChanceId != "") {
            // 表示当前是修改操作
            title = "<h2>营销机会管理 - 更新机会</h2>";
            url += "?saleChanceId=" + saleChanceId;
        }

        layui.layer.open({
            title:title,
            type:2, //  iframe层
            area:["500px","620px"], // 弹出层的宽高
            maxmin:true, // 是否可以最大化最小化
            content:url // 弹出层的内容，可以是HTMl、字符串或url地址；如果type是2，设置url
        });
    }


});
