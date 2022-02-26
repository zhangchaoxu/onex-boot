import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.dto.MailSmsSendForm;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.mail.SmsAliyunMailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("消息测试")
public class MsgTest {

    @Test
    @DisplayName("发送短信")
    void testSend() {
        MailSmsSendForm form = new MailSmsSendForm();
        form.setMailTo("13000000001");
        MailSendForm mailSendForm = ConvertUtils.sourceToTarget(form, MailSendForm.class);
        log.error("mailSendForm={}", mailSendForm);
        SmsAliyunMailService mailService = new SmsAliyunMailService();
        MailTplEntity mailTpl = new MailTplEntity();
        mailService.sendMail(mailTpl, mailSendForm);

    }
}
