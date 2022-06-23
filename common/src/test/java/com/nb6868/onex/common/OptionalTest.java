package com.nb6868.onex.common;

import cn.hutool.core.lang.Opt;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@DisplayName("Optional")
public class OptionalTest {

    @Test
    void testOptional() {
        BaseEntity entity = null;
        Long id = Optional.ofNullable(entity).orElse(new BaseEntity() {
            @Override
            public Long getId() {
                return 1L;
            }
        }).getId();
        log.error("id={}", id);
    }

    @Test
    void testOptionalBean() {
        BaseEntity entity = null;
        Long id = Opt.ofNullable(entity).peek(new Consumer<BaseEntity>() {
            @Override
            public void accept(BaseEntity baseEntity) {
                baseEntity.getCreateId();
            }
        }).orElse(new BaseEntity() {
            @Override
            public Long getId() {
                return 1L;
            }
        }).getId();
        log.error("id={}", id);
    }

}
