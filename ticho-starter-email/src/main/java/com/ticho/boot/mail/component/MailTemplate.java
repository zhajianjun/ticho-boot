package com.ticho.boot.mail.component;

import cn.hutool.core.collection.CollUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 邮件工具
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Component
@Slf4j
public class MailTemplate {

    /**
     * 发送邮件
     *
     * @param mailContent 邮件内容
     */
    public void sendSimpleMail(MailContent mailContent) {
        MailProperties mailProperties = SpringContext.getBean(MailProperties.class);
        JavaMailSender javaMailSender = SpringContext.getBean(JavaMailSender.class);
        Assert.isNotNull(mailProperties, BizErrCode.FAIL, "请检查邮件配置");
        Assert.isNotNull(javaMailSender, BizErrCode.FAIL, "请检查邮件配置");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(mailContent.getTo());
            helper.setSubject(mailContent.getSubject());
            helper.setText(mailContent.getContent(), true);
            List<String> cc = mailContent.getCc();
            if (CollUtil.isNotEmpty(cc)) {
                helper.setCc(cc.toArray(new String[0]));
            }
        } catch (MessagingException e) {
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
        javaMailSender.send(mimeMessage);
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


}
