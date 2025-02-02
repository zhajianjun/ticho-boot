package top.ticho.starter.mail.prop;

import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件配置
 *
 * @author zhajianjun
 * @date 2024-02-06 10:40
 */
@Data
public class TiMailProperty {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /** 是否开启配置 */
    private Boolean enable;
    /** 地址 */
    private String host;
    /** 端口 */
    private Integer port;
    /** 用户名 */
    private String username;
    /** 发件人名称 */
    private String senderName;
    /** 密码 */
    private String password;
    /** 协议 */
    private String protocol = "smtp";
    /** 默认编码 */
    private Charset defaultEncoding = DEFAULT_CHARSET;
    /** 其他 JavaMail 会话属性。 */
    private Map<String, String> properties = new HashMap<>();


}
