package top.ticho.starter.mail.component;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 静态资源
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40
 */
@Data
public class TiMailInines {

    /** 静态资源id */
    private String contentId;
    /** 静态资源文件 */
    private MultipartFile file;

}
