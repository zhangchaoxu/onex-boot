package com.nb6868.onex.common;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.engine.spel.SpELEngine;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.LoginForm;
import com.nb6868.onex.common.filter.xss.XssUtils;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.function.Consumer;

@DisplayName("字符串测试")
@Slf4j
public class StringTest {

    @Test
    @DisplayName("jsontest")
    void jsonTest() {
        String result = "xxx";
        JSONObject json = JacksonUtils.jsonToPojo("ssss", JSONObject.class, null);
        log.error("result={}", json.toString());
    }

    @Test
    @DisplayName("split")
    void split() {
        String result = null;
        StrUtil.splitTrim(result, ',').forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                log.error("result={}", s);
            }
        });
    }

    @Test
    @DisplayName("join")
    void join() {
        log.error(StrUtil.join("/", "", "ss", null));
    }

    @Test
    @DisplayName("aesEncode")
    void aesEncodeTest() {
        String raw = "{\"password\":\"admin\",\"tenantCode\": \"xxx\",\"type\":\"ADMIN_USERNAME_PASSWORD\",\"username\":\"admin\"}";
        String content = SecureUtil.aes(Const.AES_KEY.getBytes()).encryptBase64(raw);
        EncryptForm form = new EncryptForm();
        form.setBody(content);
        log.error("form=" + form);
    }

    @Test
    @DisplayName("aesDecode")
    void aesDecodeTest() {
        String raw = "bqJOFdO/IlOCMHJ6V+BDpyVlY1N5opy5uOrww4u/6huTIK7XB5WVAGiYflVn5AmzeLCpaiXQxUorBW3P05kexppCz3Y8uSi7W7NpWWWc7wY3OOT4aLKLZwylNiorEz5S";
        EncryptForm form = new EncryptForm();
        form.setBody(raw);
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(form.getBody());
        log.error("json=" + json);
    }

    @Test
    @DisplayName("passwordTest")
    void passwordTest() {
        String reg = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
        String p1 = "admin@2022";
        String p2 = "admin2022";
        String p3 = "admin@@";
        String p4 = "123456##";
        String p5 = "admiadmin";
        String p6 = "1234567999";
        log.error("p1=" + ReUtil.isMatch(reg, p1));
        log.error("p2=" + ReUtil.isMatch(reg, p2));
        log.error("p3=" + ReUtil.isMatch(reg, p3));
        log.error("p4=" + ReUtil.isMatch(reg, p4));
        log.error("p5=" + ReUtil.isMatch(reg, p5));
        log.error("p6=" + ReUtil.isMatch(reg, p6));
        log.error("1=" + ReUtil.isMatch("^1325242198", "13252421988"));
        log.error("2=" + ReUtil.isMatch("\\d{4}$", "123456"));
        log.error("3=" + ReUtil.isMatch("\\d{4}$", "1234"));
    }

    @Test
    @DisplayName("passwordEncode")
    void passwordEncode() {
        String raw = "123456";
        log.error("password={}", PasswordUtils.encode(raw));
        log.error("password={}", PasswordUtils.aesEncode(raw, Const.AES_KEY));
    }

    @Test
    @DisplayName("passwordDecode")
    void passwordDecode() {
        String password = "AJaZWYDQIVOBOIWOM0jaVw==";
        log.error("password={}", PasswordUtils.aesDecode(password, Const.AES_KEY));
    }

    @Test
    @DisplayName("array utils")
    void arrayTest() {
        String[] arrays = null;
        String from = "page";
        log.error("res={}", ArrayUtil.contains(arrays, from));
    }

    @Test
    @DisplayName("ase")
    void aes() {
        String str = "0PxiZS8Xb8MO97iRQMsNIiQ6CE%2F4NMntbkGWexgySMVZSgUTvcIgynxMomNQE4vsxnEAL7cLcGPRm96NA1cR%2Bbek19dIl37P6M%2FTSeOogABXI70GxODX9WfiKrWEyU%2BE28apfsgyL0VHoMtzHH30SkpjhQ0upDMUDIo9WyxNpbieEsQ17RamHhWeDdQEK7Jsot0vxj%2F2F8sPUYdoyuz%2BCw%3D%3D";
        String str2 = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(URLUtil.decode(str));
        log.error(str2);
    }

    @Test
    @DisplayName("正则匹配ip")
    void matchIp() {
        String content = "10.0.0.99";
        boolean isMatch = ReUtil.isMatch("10.0.0.(\\d{1,3})", content);
        boolean isMatch2 = ReUtil.isMatch("10.0.0.199", content);
        log.error("isMatch={}", isMatch);
        log.error("isMatch2={}", isMatch2);
    }

    @Test
    @DisplayName("接口签名")
    void signApi() {
        // 加密字符串
        String secret = "znrlZErG74WKWv6VLLbKFmUQ93VebesE";
        // 接口参数
        Map<String, Object> apiParams = new HashMap<>();
        apiParams.put("_t", System.currentTimeMillis());
        apiParams.put("clientkey", "K3OWcL9PBqzavTdrmlUDmID0FTnbUvdN");
        String apiParamSplit = ""; // "&"
        String apiParamJoin = ""; // "="
        // 接口Body
        Object apiBody = "";
        // 接口方法
        String apiMethod = "get";
        // 接口路径
        String apiPath = "/v1/devices";

        // 1. 参数拼接排序后拼接
        StrJoiner paramJoin = new StrJoiner(apiParamJoin);
        MapUtil.sort(apiParams).forEach((key, value) -> {
            // 部分字段不加入签名
            if (!"sign".equalsIgnoreCase(key)) {
                paramJoin.append(key + apiParamSplit + value);
            }
        });
        String paramJoin2 = SignUtils.paramToQueryString(apiParams, "", "", true);
        log.info("参数拼接,字符串={}", paramJoin);
        log.info("参数拼接,字符串2={}", paramJoin2);
        log.info("参数拼接+body,字符串={}", paramJoin + apiBody.toString());
        log.info("参数拼接+body+method,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase());
        log.info("参数拼接+body+method+url,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase() + apiPath);
        String rawString = secret + paramJoin + apiBody + apiMethod.toUpperCase() + apiPath + secret;
        log.info("加密前明文,字符串={}", rawString);
        log.info("##############");
        String md5Hutool = SecureUtil.md5(rawString);
        String md5Spring = DigestUtils.md5DigestAsHex(rawString.getBytes());
        // 3b9f4c773c7818b81d793ed4830401ab
        log.info("hutool最终结果32位16进制md5,sign={}", md5Hutool);
        log.info("java最终结果32位16进制md5,sign={}", md5Spring);
        log.info("https://api.zillionsource.com/v1/devices?clientkey={}&_t={}&sign={}", MapUtil.getStr(apiParams, "clientkey"), MapUtil.getStr(apiParams, "_t"), md5Hutool);
    }

    @Test
    @DisplayName("测试")
    void test() {
        // 加密字符串
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";
        log.error("1={}", ReUtil.isMatch(regex, "znrlZErG74WKWv6VLLbKFmUQ93VebesE"));
        log.error("1={}", ReUtil.isMatch(regex, "zas!@#2"));
        log.error("1={}", ReUtil.isMatch(regex, "1121asdsa"));
        log.error("1={}", ReUtil.isMatch(regex, "!@1121asdsa!!"));
        log.error("1={}", ReUtil.isMatch(regex, "!@11223232312sfadsfs张撒打算打算的安顺1asdsa!!"));
    }

    @Test
    void jwtEncode() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30);
        Date expiresDate = nowTime.getTime();

        byte[] key = "1234567890".getBytes();
        String jwtToken = JWT.create()
                .setPayload("sub", "1234567890")
                .setPayload("id", 1118075560757063681L)
                .setPayload("name", "looly")
                .setPayload("admin", true)
                //.setSigner("XXXYYY", key)
                .setKey(key)
                .setExpiresAt(expiresDate)
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .sign();
        System.out.println("jwtToken=" + jwtToken);
    }

    @Test
    void jwtDecode() {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOjExMTgwNzU1NjA3NTcwNjM2ODEsIm5hbWUiOiJsb29seSIsImFkbWluIjp0cnVlLCJleHAiOjE2NDg1MjQ3MzcsImlhdCI6MTY0ODUyMjkzOCwibmJmIjoxNjQ4NTIyOTM4fQ.kPJCrGFSVWpSicBpbplRH8tqlx-q_LVsLZX4xPyevuU";
        JWT jwt = JWT.of(jwtToken);
        System.out.println("Header=" + jwt.getHeader());
        System.out.println("Payload=" + jwt.getPayload());
        System.out.println("ALGORITHM=" + jwt.getAlgorithm());
        System.out.println("Signer=" + jwt.getSigner());
        // verify只验证内容，不验证时间
        System.out.println("verify=" + jwt.setKey("1234567890".getBytes()).validate(0));
        System.out.println("verify=" + jwt.setKey("1234567890".getBytes()).verify());
    }

    @Test
    @DisplayName("测试EL表达式")
    void elTest() {
        ExpressionEngine engine = new SpELEngine();
        final Dict dict = Dict.create()
                .set("a", 100.3)
                .set("b", 45)
                .set("c", -199.100);
        LoginForm loginForm = new LoginForm();
        loginForm.setType("sss");
        loginForm.setSms("sms2232");
        final Object eval = engine.eval("#a-(#b-#c)", dict, null);
        final Object eval2 = BeanUtil.getProperty(loginForm, "sms");
        log.error("eval={}", eval);
        log.error("eval2={}", eval2);
    }

    @Test
    @DisplayName("xss过滤器测试")
    void xssTest() {
        String raw = "{\"id\":\"1567452017224237057\",\"type\":1,\"title\":\"<p>我国消防工作的原则是()<img src=\\\"https://oos-cn.ctyunapi.cn/anbao-public/20221103/avatar.png\\\"></p>\",\"optionsA\":\"政府统一领导、部门依法监管、单位有限责任、公民广泛参与\",\"optionsB\":\"部门依法领导、政府统一监管、单位全面负责、公民积极参与\",\"optionsC\":\"政府统一领导、部门全面负责、单位依法管理、公民积极参与\",\"optionsD\":\"政府统一领导、部门依法监管、单位全面负责、公民积极参与\",\"optionsE\":null,\"optionsF\":null,\"optionsCorrect\":\"D\",\"content\":\"\",\"tags\":null,\"relType\":\"course\",\"relId\":\"1556192543100547074\",\"createId\":\"1510103618900885505\",\"createTime\":\"2022-09-07 17:57:37\",\"updateId\":\"1510103618900885505\",\"updateTime\":\"2022-11-03 12:07:28\",\"sort\":0,\"options\":null,\"score\":0,\"imgs\":null,\"remark\":null,\"tenantCode\":null,\"relName\":\"消防基础知识\"}";
        String raw2 = "<p>我国消防工作的原则是()<img src=\\\"https://oos-cn.ctyunapi.cn/anbao-public/20221103/avatar.png\\\"></p>";
        String txt = XssUtils.filter(raw);
        String txt2 = XssUtils.filter(raw2);
        log.error("raw={}", raw);
        log.error("txt={}", txt);
        log.error("txt2={}", txt2);
    }

    @Test
    @DisplayName("xss过滤器测试")
    void hutoolXssTest() {
        String raw = "{\"id\":\"1567452017224237057\",\"type\":1,\"title\":\"<p>我国消防工作的原则是()<img src=\\\"https://oos-cn.ctyunapi.cn/anbao-public/20221103/avatar.png\\\"></p>\",\"optionsA\":\"政府统一领导、部门依法监管、单位有限责任、公民广泛参与\",\"optionsB\":\"部门依法领导、政府统一监管、单位全面负责、公民积极参与\",\"optionsC\":\"政府统一领导、部门全面负责、单位依法管理、公民积极参与\",\"optionsD\":\"政府统一领导、部门依法监管、单位全面负责、公民积极参与\",\"optionsE\":null,\"optionsF\":null,\"optionsCorrect\":\"D\",\"content\":\"\",\"tags\":null,\"relType\":\"course\",\"relId\":\"1556192543100547074\",\"createId\":\"1510103618900885505\",\"createTime\":\"2022-09-07 17:57:37\",\"updateId\":\"1510103618900885505\",\"updateTime\":\"2022-11-03 12:07:28\",\"sort\":0,\"options\":null,\"score\":0,\"imgs\":null,\"remark\":null,\"tenantCode\":null,\"relName\":\"消防基础知识\"}";
        String raw2 = "<p>我国消防工作的原则是()<img src=\"https://oos-cn.ctyunapi.cn/anbao-public/20221103/avatar.png\"></p><alert> sss</alert>";
        String txt = HtmlUtil.filter(raw);
        String txt2 = HtmlUtil.filter(raw2);
        log.error("raw={}", raw);
        log.error("txt={}", txt);
        log.error("txt2={}", txt2);
    }

    @Test
    @DisplayName("路径测试")
    void pathTest() {
        String ossFileStoragePath = "/oss/";
        log.error("normalize={}", FileUtil.normalize(ossFileStoragePath));
        log.error("isAbsolutePath={}", FileUtil.isAbsolutePath(FileUtil.normalize(ossFileStoragePath)));

    }

    @Test
    @DisplayName("缓存测试")
    void cacheTest() {
        //创建缓存，默认4毫秒过期
        TimedCache<String, String> timedCache = CacheUtil.newTimedCache(4);
    }

    @Test
    @DisplayName("缓存测试")
    void format() {
        String tpl = "您的验证码为：{code}-{test}，请勿泄漏验证码。";
        //创建缓存，默认4毫秒过期
        Map<String, Object> map = new HashMap<>();
        map.put("code", "1234");
        map.put("test", null);
        log.error(StrUtil.format(tpl, map, true));
        log.error(StrUtil.format(tpl, map, false));
    }

    @Test
    @DisplayName("时间测试")
    void dateTest() {
        String fmt = DateUtil.format(new Date(), FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT+08:00")));
        String fmt2 = DateUtil.formatHttpDate(new Date());
        log.error("fmt={}", fmt);
        log.error("fmt2={}", fmt2);
    }

}
