package top.ticho.boot.mail.component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 静态资源
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Data
@ApiModel("静态资源")
public class MailInines {

    @ApiModelProperty(value = "静态资源id", position = 10)
    private String contentId;

    @ApiModelProperty(value = "静态资源文件", position = 20)
    private MultipartFile file;

}
