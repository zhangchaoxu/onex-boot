package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.dialect.Props;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.TimeZone;

@DisplayName("阿里云OSS接口测试")
@Slf4j
public class AliyunOssApiTest {

    @Test
    @DisplayName("文件上传")
    void uploadFile() {
        //AliyunOssApi.putObject("");
        log.error(FileUtil.getMimeType("/2/1"));
        log.error(FileUtil.getMimeType("1.png"));
        log.error(FileUtil.getMimeType("1.jpeg"));
        log.error(FileUtil.getMimeType("1.txt"));
        log.error(FileUtil.getMimeType("1.md"));
        String path = "D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".txt";
        FileUtil.writeUtf8String("sss", path);

        FileUtil.file("D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".txt");
        Props props = new Props("confidential/oss.properties");
        ApiResult<JSONObject> result = AliyunOssApi.putObject(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "test/换行 " + IdUtil.fastSimpleUUID() + ".txt", null, FileUtil.getInputStream(path));
        log.error(result.toString());
    }

    @Test
    @DisplayName("文件下载")
    void downloadFile() {
        // FileUtil.file("D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".pdf");
        Props props = new Props("confidential/oss.properties");
        ApiResult<InputStream> result = AliyunOssApi.getObject(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "20240226/《网络销售特殊食品安全合规指南》.pdf", null);
        log.error(result.isSuccess() + "");
        if (result.isSuccess()) {
            FileUtil.writeFromStream(result.getData(), "D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".pdf");
        }
    }

    @Test
    @DisplayName("生成预签名链接")
    void getPreSignUrl() {
        Props props = new Props("confidential/oss.properties");

        ApiResult<String> result = AliyunOssApi.getPreSignedUrl(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "test/换行 ff2ad93956484509912dbe440f324551.txt", null, "put", 10 * 60);
        log.error(result.toString());
        log.error(result.getData());
    }

    @Test
    @DisplayName("生成post form信息")
    void getSignPostForm() {
        Props props = new Props("confidential/oss.properties");

        JSONArray conditions = new JSONArray();
        Date date = DateUtil.date();
        JSONObject policy = new JSONObject();
        // https://help.aliyun.com/zh/oss/developer-reference/signature-version-4-recommend
        // 用于指定policy的过期时间，以ISO8601 GMT时间表示
        policy.set("expiration", DateUtil.format(DateUtil.offsetSecond(DateUtil.date(), 600), FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_MS_FORMAT, TimeZone.getTimeZone("GMT"))));
        // 指定POST请求表单域的合法值
        conditions = ObjUtil.defaultIfNull(conditions, new JSONArray());
        conditions.add(new JSONObject().set("bucket", props.getStr("bucketName")));
        conditions.add(new JSONObject().set("x-oss-signature-version", AliyunOssApi.OSS4_HMAC_SHA256));
        conditions.add(new JSONObject().set("x-oss-credential", StrUtil.format("{}/{}/{}/oss/aliyun_v4_request", props.getStr("accessKeyId"), DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT"))), props.getStr("region"))));
        conditions.add(new JSONObject().set("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT")))));
        policy.set("conditions", conditions);
        // 签名
        String sign = AliyunOssApi.postSignV4(date, props.getStr("region"), props.getStr("accessKeySecret"), policy);
        JSONObject result = new JSONObject();
        // 访问地址
        result.set("host",  StrUtil.format("{}.{}", props.getStr("bucketName"), StrUtil.emptyToDefault(props.getStr("endPoint"), props.getStr("endPoint"))));
        // header
        JSONObject form = new JSONObject();
        form.set("policy", Base64.encode(policy.toString()));
        form.set("x-oss-signature-version", AliyunOssApi.OSS4_HMAC_SHA256);
        form.set("x-oss-credential", StrUtil.format("{}/{}/{}/oss/aliyun_v4_request", props.getStr("accessKeyId"), DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT"))), props.getStr("region")));
        form.set("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT"))));
        form.set("x-oss-signature", sign);
        result.set("form", form);
        log.error(result.toStringPretty());
    }
}
