layui.extend({
  admin: '{/}../../js/admin'
});
layui.use(['form', 'layer', 'admin'], function () {
  var form = layui.form,
    admin = layui.admin,
    layer = layui.layer;

  //监听提交
  form.on('submit(add)', function (data) {
    console.log(data);
    //发异步，把数据提交给php
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

});
