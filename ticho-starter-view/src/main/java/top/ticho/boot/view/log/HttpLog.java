package top.ticho.boot.view.log;

import cn.hutool.http.useragent.UserAgent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


/**
 * 接口日志
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class HttpLog {

    /** 请求类型 */
    private String type;

    /** ip */
    private String ip;

    /** 请求地址 */
    private String url;

    /** 端口号 */
    private String port;

    /** 全路径接口 */
    private String fullUrl;

    /** 请求参数 */
    private String reqParams;

    /** 请求体 */
    private String reqBody;

    /** 请求头 */
    private String reqHeaders;

    /** 响应体 */
    private String resBody;

    /** 响应头 */
    private String resHeaders;

    /** 响应状态 */
    private Integer status;

    /* 请求开始时间戳 */
    private Long start;

    /* 请求结束时间戳 */
    private Long end;

    /* 请求间隔 */
    private Long consume;

    /* 用户信息 */
    private String username;

    /* User-Agent信息对象 */
    private UserAgent userAgent;

    public Long getConsume() {
        if (start == null || end == null) {
            return 0L;
        }
        return end - start;
    }

    public String getFullUrl() {
        if (Objects.nonNull(fullUrl)) {
            return fullUrl;
        }
        if (url == null || port == null || ip == null) {
            return null;
        }
        return String.format("%s:%s%s", ip, port, url);
    }

}
