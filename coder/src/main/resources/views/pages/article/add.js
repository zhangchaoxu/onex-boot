/*
 * @Author: https://github.com/WangEn
 * @Author: https://gitee.com/lovetime/
 * @Date:   2020-07-30
 * @lastModify 2020-07-30 16:23:47
 * +----------------------------------------------------------------------
 * | Weadmin [ 后台管理模板 ]
 * | 基于Layui http://www.layui.com/
 * +----------------------------------------------------------------------
 */
layui.use(['form', 'layer'], function () {
  var form = layui.form,
    layer = layui.layer;

  //监听提交
  form.on('submit(add)', function (data) {
    console.log(data);
    //发异步，把数据提交给php
    layer.alert("增加成功", { icon: 6 }, function () {
      // 获得frame索引
      var index = parent.layer.getFrameIndex(window.name);
      //关闭当前frame
      parent.layer.close(index);
    });
    return false;
  });
});