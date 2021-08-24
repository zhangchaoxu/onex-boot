/*
 * @Author: https://github.com/WangEn
 * @Author: https://gitee.com/lovetime/
 * @Date:   2018-01-01
 * @lastModify 2020-07-30 16:23:47
 * +----------------------------------------------------------------------
 * | Weadmin [ 后台管理模板 ]
 * | 基于Layui http://www.layui.com/
 * +----------------------------------------------------------------------
 */
//自定义的render渲染输出多列表格
var layout = [{
  name: '菜单名称',
  treeNodes: true,
  headerClass: 'value_col',
  colClass: 'value_col',
  style: 'width: 60%'
},
{
  name: '状态',
  headerClass: 'td-status',
  colClass: 'td-status',
  style: 'width: 10%',
  render: function () {
    return '<span class="layui-btn layui-btn-normal layui-btn-xs">已启用</span>';
  }
},
{
  name: '操作',
  headerClass: 'td-manage',
  colClass: 'td-manage',
  style: 'width: 20%',
  render: function (row) {
    return '<a onclick="node_stop(this,\'10001\')" href="javascript:;" title="启用"><i class="layui-icon">&#xe601;</i></a>' +
      '<a title="添加子类" onclick="WeAdminShow(\'添加\',\'./category-add.html\')" href="javascript:;"><i class="layui-icon">&#xe654;</i></a>' +
      '<a title="编辑" onclick="WeAdminShow(\'编辑\',\'./category-edit.html\')" href="javascript:;"><i class="layui-icon">&#xe642;</i></a>' +
      '<a title="删除" onclick="node_del(' + row.id + ')" href="javascript:;">\<i class="layui-icon">&#xe640;</i></a>';
  }
},
];
//加载扩展模块 treeGrid
layui.config({
  base: '../../js/'
  , version: '101100'
}).extend({
  admin: 'admin',
  treeGrid: 'extends/treeGrid'
});
// layui.extend({
//   admin: '{/}../../static/js/admin',
//   treeGrid: '{/}../../static/js/extends/treeGrid' // {/}的意思即代表采用自有路径，即不跟随 base 路径
// });
layui.use(['treeGrid','admin', 'jquery', 'layer'], function () {
  var layer = layui.layer,
    $ = layui.jquery,
		admin = layui.admin,
    treeGrid = layui.treeGrid;
  var baseApiUrl = 'https://www.fastmock.site/mock/aec70da5ba0d047d3929ec1bac48460f/admin';
  var nodeTree = null;

  var renderList = function () {
    $.ajax({
      url: baseApiUrl + '/article/types',
      type: 'GET',
      data: {
        username: localStorage.getItem('username'),
      },
      success: function (res) {
        if (res.code === 1) {
          // 数据处理示例，可省略
          var initialData = res.data.result;
          var resultDate = initialData.map(function (item) {
            return Object.assign({}, item, { spread: true });
          });

          // 渲染树节点
          nodeTree = treeGrid({
            elem: '#demo'
            , spreadable: true //设置是否全展开，默认不展开
            , nodes: resultDate
            , layout: layout
          });
        } else {
          layer.msg(res.msg, function () { });
        }
      }
    })
  }

  window.node_del = function (nodeId) {
    alert(nodeId);
  }
  /*分类-停用*/
  window.node_stop = function (obj, id) {
    var confirmTip;
    if ($(obj).attr('title') == '启用') {
      confirmTip = '确认要停用吗？';
    } else {
      confirmTip = '确认要启用吗？';
    }
    layer.confirm(confirmTip, function (index) {
      if ($(obj).attr('title') == '启用') {
        //发异步把用户状态进行更改
        $(obj).attr('title', '停用')
        $(obj).find('i').html('&#xe62f;');
        $(obj).parents("tr").find(".td-status").find('span').addClass('layui-btn-disabled').html('已停用');
        layer.msg('已停用!', {
          icon: 5,
          time: 1000
        });
      } else {
        $(obj).attr('title', '启用')
        $(obj).find('i').html('&#xe601;');

        $(obj).parents("tr").find(".td-status").find('span').removeClass('layui-btn-disabled').html('已启用');
        layer.msg('已启用!', {
          icon: 6,
          time: 1000
        });
      }
    });
  }

  $(function () {
    renderList();
  });

  $('#collapse').on('click', function () {
    layui.collapse(nodeTree);
  });

  $('#expand').on('click', function () {
    layui.expand(nodeTree);
  });
});
