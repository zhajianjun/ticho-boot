package top.ticho.boot.mail.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;
import top.ticho.boot.mail.prop.MailProperty;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
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
 * @date 2022-07-13 22:40:25
 */
@Slf4j
public class MailTemplate {

    private final MailProperty mailProperty;

    private final JavaMailSender javaMailSender;

    public MailTemplate(MailProperty mailProperty) {
        this.mailProperty = mailProperty;
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(mailProperty, sender);
        this.javaMailSender = sender;
    }

    /**
     * 发送邮件
     *
     * @param mailContent 邮件内容
     */
    public void sendMail(MailContent mailContent) {
        Assert.isNotNull(mailProperty, BizErrCode.FAIL, "请检查邮件配置");
        Assert.isNotNull(javaMailSender, BizErrCode.FAIL, "请检查邮件配置");
        MimeMessage mimeMessage = getMimeMessage(mailContent);
        javaMailSender.send(mimeMessage);
    }

    private MimeMessage getMimeMessage(MailContent mailContent) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            String senderName = StrUtil.isBlank(mailProperty.getSenderName()) ? mailProperty.getUsername() : mailProperty.getSenderName();
            helper.setFrom(mailProperty.getUsername(), senderName);
            helper.setTo(mailContent.getTo());
            helper.setSubject(mailContent.getSubject());
            helper.setText(mailContent.getContent(), true);
            List<String> cc = mailContent.getCc();
            if (CollUtil.isNotEmpty(cc)) {
                helper.setCc(cc.toArray(new String[0]));
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            Assert.cast(BizErrCode.FAIL, "创建邮件MimeMessageHelper失败");
        }
        List<MailInines> inlines = mailContent.getInlines();
        if (CollUtil.isNotEmpty(inlines)) {
            MimeMessageHelper finalHelper = helper;
            inlines.forEach(inline -> addInline(finalHelper, inline));
        }
        List<MultipartFile> files = mailContent.getFiles();
        if (CollUtil.isNotEmpty(files)) {
            MimeMessageHelper finalHelper = helper;
            files.forEach(file -> addAttachment(finalHelper, file));
        }
        return mimeMessage;
    }

    public void sendMailBatch(List<MailContent> mailContents) {
        Assert.isNotNull(mailProperty, BizErrCode.FAIL, "请检查邮件配置");
        Assert.isNotNull(javaMailSender, BizErrCode.FAIL, "请检查邮件配置");
        MimeMessage[] mimeMessages = mailContents
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
            Assert.cast(BizErrCode.FAIL, "添加附件资源失败");
        }
    }

    private void addInline(@NonNull MimeMessageHelper helper, @NonNull MailInines inline) {
        MultipartFile file = inline.getFile();
        try {
            ByteArrayDataSource iss = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
            helper.addInline(inline.getContentId(), iss);
        } catch (MessagingException | IOException e) {
            log.error(e.getMessage(), e);
            Assert.cast(BizErrCode.FAIL, "添加邮件静态资源失败");
        }
    }

    private void applyProperties(MailProperty mailProperty, JavaMailSenderImpl sender) {
        sender.setHost(mailProperty.getHost());
        if (mailProperty.getPort() != null) {
            sender.setPort(mailProperty.getPort());
        }
        sender.setUsername(mailProperty.getUsername());
        sender.setPassword(mailProperty.getPassword());
        sender.setProtocol(mailProperty.getProtocol());
        if (mailProperty.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(mailProperty.getDefaultEncoding().name());
        }
        if (!mailProperty.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(mailProperty.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }


}
