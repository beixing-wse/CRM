layui.use(['table', 'treetable'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treeTable = layui.treetable;
    treeTable.render({
        treeColIndex: 1,
        treeSpid: -1,
        treeIdName: 'id',
        treePidName: 'parentId',
        elem: '#munu-table',
        url: ctx+'/module/list',
        toolbar: "#toolbarDemo",
        treeDefaultClose:true,
        page: true,
        cols: [[
            {type: 'numbers'},
            {field: 'moduleName', minWidth: 100, title: '菜单名称'},
            {field: 'optValue', title: '权限码'},
            {field: 'url', title: '菜单url'},
            {field: 'createDate', title: '创建时间'},
            {field: 'updateDate', title: '更新时间'},
            {
                field: 'grade', width: 80, align: 'center', templet: function (d) {
                    if (d.grade == 0) {
                        return '<span class="layui-badge layui-bg-blue">⽬录</span>';
                    }
                    if(d.grade==1){
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                    if (d.grade == 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#auth-state', width: 220, align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });

    /**
     * 监听头工具栏
     */
    table.on('toolbar(munu-table)', function(obj){
        switch(obj.event){
            case "expand":
                //全部展开
                treeTable.expandAll('#munu-table');
                break;
            case "fold":
                //全部折叠
                treeTable.foldAll('#munu-table');
                break;
            case "add":
                //添加模块几率
                openAddModuleDialog(0, -1);
        };
    });

    /**
     * 行工具栏
     */
    table.on('tool(munu-table)', function (obj) {
        if (obj.event == "add") {
            // 三级菜单不可添加子项
            if (obj.data.grade == 2) {
                layer.msg("暂不支持四级菜单！",{icon:5});
                return;
            }
            // 添加子项
            openAddModuleDialog(obj.data.grade + 1, obj.data.id);

        } else if (obj.event == "edit") {
            // 更新资源
            openUpdateModuleDialog(obj.data.id);

        } else if (obj.event == "del") {
            // 删除操作
            layer.confirm('确定删除当前菜单？', {icon: 3, title: "菜单管理"}, function (index) {
                // 发送ajax请求
                $.post(ctx + "/module/delete",{mId:obj.data.id}, function (data) {
                    if (data.code == 200) {
                        layer.msg("删除成功！",{icon:6});
                        window.location.reload();
                    } else {
                        layer.msg(data.msg, {icon:5});
                    }
                });
            });
        }
    });


    /**
     * 打开添加模块的弹出层
     * @param grade     层级码
     * @param parentId  父层级id
     */
    function openAddModuleDialog(grade, parentId) {
        var url  =  ctx+"/module/addModulePage?grade="+grade+"&parentId="+parentId;
        var title="菜单添加";
        layui.layer.open({
            title : title,
            type : 2,
            area:["700px","450px"],
            maxmin:true,
            content : url
        });
    }

    /**
     * 打开更新资源的弹出层
     * @param id
     */
    function openUpdateModuleDialog(id){
        var url  =  ctx+"/module/updateModulePage?id="+id;
        var title="菜单更新";
        layui.layer.open({
            title : title,
            type : 2,
            area:["700px","450px"],
            maxmin:true,
            content : url
        });
    }
});
