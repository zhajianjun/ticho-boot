package top.ticho.boot.mail.component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("邮件内容")
public class MailContent {

    @ApiModelProperty(value = "收件人地址", position = 20)
    private String to;

    @ApiModelProperty(value = "邮件主题", position = 30)
    private String subject;

    @ApiModelProperty(value = "邮件内容", position = 40)
    private String content;

    @ApiModelProperty(value = "抄送地址", position = 50)
    private List<String> cc;

    @ApiModelProperty(value = "静态资源列表", position = 60)
    private List<MailInines> inlines;

    @ApiModelProperty(value = "附件信息", position = 70)
    private List<MultipartFile> files;

}
