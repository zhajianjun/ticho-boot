package top.ticho.starter.mail.component;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 邮件内容
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Data
public class TiMailContent {
    /** 收件人地址 */
    private String to;
    /** 邮件主题 */
    private String subject;
    /** 邮件内容 */
    private String content;
    /** 抄送地址 */
    private List<String> cc;
    /** 静态资源列表 */
    private List<TiMailInines> inlines;
    /** 附件信息 */
    private List<MultipartFile> files;

}
