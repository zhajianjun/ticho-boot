package top.ticho.starter.mail.component;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;
import top.ticho.starter.mail.prop.TiMailProperty;
import top.ticho.starter.view.util.TiAssert;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.enums.TiBizErrorCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 邮件工具
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40
 */
@Slf4j
public class TiMailTemplate {

    private final TiMailProperty tiMailProperty;

    private final JavaMailSender javaMailSender;

    public TiMailTemplate(TiMailProperty tiMailProperty) {
        this.tiMailProperty = tiMailProperty;
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(tiMailProperty, sender);
        this.javaMailSender = sender;
    }

    /**
     * 发送邮件
     *
     * @param tiMailContent 邮件内容
     */
    public void sendMail(TiMailContent tiMailContent) {
        TiAssert.isNotNull(tiMailProperty, TiBizErrorCode.FAIL, "请检查邮件配置");
        TiAssert.isNotNull(javaMailSender, TiBizErrorCode.FAIL, "请检查邮件配置");
        MimeMessage mimeMessage = getMimeMessage(tiMailContent);
        javaMailSender.send(mimeMessage);
    }

    private MimeMessage getMimeMessage(TiMailContent tiMailContent) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            String senderName = TiStrUtil.isBlank(tiMailProperty.getSenderName()) ? tiMailProperty.getUsername() : tiMailProperty.getSenderName();
            helper.setFrom(tiMailProperty.getUsername(), senderName);
            helper.setTo(tiMailContent.getTo());
            helper.setSubject(tiMailContent.getSubject());
            helper.setText(tiMailContent.getContent(), true);
            List<String> cc = tiMailContent.getCc();
            if (TiCollUtil.isNotEmpty(cc)) {
                helper.setCc(cc.toArray(new String[0]));
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            TiAssert.cast(TiBizErrorCode.FAIL, "创建邮件MimeMessageHelper失败");
        }
        List<TiMailInines> inlines = tiMailContent.getInlines();
        if (TiCollUtil.isNotEmpty(inlines)) {
            MimeMessageHelper finalHelper = helper;
            inlines.forEach(inline -> addInline(finalHelper, inline));
        }
        List<MultipartFile> files = tiMailContent.getFiles();
        if (TiCollUtil.isNotEmpty(files)) {
            MimeMessageHelper finalHelper = helper;
            files.forEach(file -> addAttachment(finalHelper, file));
        }
        return mimeMessage;
    }

    public void sendMailBatch(List<TiMailContent> tiMailContents) {
        TiAssert.isNotNull(tiMailProperty, TiBizErrorCode.FAIL, "请检查邮件配置");
        TiAssert.isNotNull(javaMailSender, TiBizErrorCode.FAIL, "请检查邮件配置");
        MimeMessage[] mimeMessages = tiMailContents
            .stream()
            .map(this::getMimeMessage)
            .toArray(MimeMessage[]::new);
        javaMailSender.send(mimeMessages);
    }

    private void addAttachment(@NonNull MimeMessageHelper finalHelper, @NonNull MultipartFile file) {
        try {
            ByteArrayDataSource iss = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
            String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("未知文件名");
            finalHelper.addAttachment(originalFilename, iss);
        } catch (MessagingException | IOException e) {
            log.error(e.getMessage(), e);
            TiAssert.cast(TiBizErrorCode.FAIL, "添加附件资源失败");
        }
    }

    private void addInline(@NonNull MimeMessageHelper helper, @NonNull TiMailInines inline) {
        MultipartFile file = inline.getFile();
        try {
            ByteArrayDataSource iss = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
            helper.addInline(inline.getContentId(), iss);
        } catch (MessagingException | IOException e) {
            log.error(e.getMessage(), e);
            TiAssert.cast(TiBizErrorCode.FAIL, "添加邮件静态资源失败");
        }
    }

    private void applyProperties(TiMailProperty tiMailProperty, JavaMailSenderImpl sender) {
        sender.setHost(tiMailProperty.getHost());
        if (tiMailProperty.getPort() != null) {
            sender.setPort(tiMailProperty.getPort());
        }
        sender.setUsername(tiMailProperty.getUsername());
        sender.setPassword(tiMailProperty.getPassword());
        sender.setProtocol(tiMailProperty.getProtocol());
        if (tiMailProperty.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(tiMailProperty.getDefaultEncoding().name());
        }
        if (!tiMailProperty.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(tiMailProperty.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }


}
