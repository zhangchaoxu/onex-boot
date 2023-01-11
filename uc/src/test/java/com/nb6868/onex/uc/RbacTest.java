package com.nb6868.onex.uc;

import com.nb6868.onex.uc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"common", "test"})
@DisplayName("RBAC")
@Slf4j
public class RbacTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("testDb")
    void testDb() {
        userService.query().last("limit 10").list().forEach(userEntity -> {
            log.error("user id = {}", userEntity.getId());
            log.error("user email = {}", userEntity.getEmail());
        });
    }

}
