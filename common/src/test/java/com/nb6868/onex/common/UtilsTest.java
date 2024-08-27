package com.nb6868.onex.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@DisplayName("工具类测试测试")
@Slf4j
public class UtilsTest {

    @Test
    @DisplayName("列表去重")
    void listDistict() {
        List<JSONObject> userList = new ArrayList<>();
        userList.add(new JSONObject().set("userid", 3).set("name", "张三3"));
        userList.add(new JSONObject().set("userid", 4).set("name", "张三4"));
        userList.add(new JSONObject().set("userid", 5).set("name", "张三5"));
        userList.add(new JSONObject().set("userid", 3).set("name", "张三3"));
        userList.add(new JSONObject().set("userid", 4).set("name", "张三4"));
        List<JSONObject> userList2 = CollUtil.distinct(userList, (Function<JSONObject, Object>) entries -> entries.getStr("userid"), true);
        log.error("xx.si={}", userList2.size());
    }

}
