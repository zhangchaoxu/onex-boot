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
layui.use(['jquery', 'form', 'layer'], function () {
  var $ = layui.jquery,
    form = layui.form,
    layer = layui.layer;

  //监听提交
  form.on('submit(add)', function (data) {
    console.log(data.field);
    //发异步，把数据提交给服务端
    layer.alert("增加成功", {
      icon: 6
    }, function () {
      // 获得frame索引
      var index = parent.layer.getFrameIndex(window.name);
      //关闭当前frame
      parent.layer.close(index);
    });
    return false;
  });

  //遍历select option
  $(document).ready(function () {
    $("#pid-select option").each(function (text) {
      var level = $(this).attr('data-level');
      var text = $(this).text();
      console.log(text);
      if (level > 0) {
        text = "├　" + text;
        for (var i = 0; i < level; i++) {
          text = "　　" + text;　//js中连续显示多个空格，需要使用全角的空格
          //console.log(i+"text:"+text);
        }
      }
      $(this).text(text);

    });

    form.render('select'); //刷新select选择框渲染
  });

});