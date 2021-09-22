import cn.hutool.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@DisplayName("JWT")
public class JwtTest {

    @Test
    void encode() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30);
        Date expiresDate = nowTime.getTime();

        byte[] key = "e9HCUI@cYR".getBytes();
        String jwtToken = JWT.create()
                .setPayload("id", 1118075560757063682L)
                .setKey(key)
                .setSubject("SHOP_WXMA_PHONE")
                .setExpiresAt(expiresDate)
                .sign();
        System.out.println("jwtToken=" + jwtToken);
    }

}
