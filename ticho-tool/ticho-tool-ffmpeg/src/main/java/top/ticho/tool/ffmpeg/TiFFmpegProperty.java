package top.ticho.tool.ffmpeg;

import lombok.Data;

/**
 * FFmpeg配置
 *
 * @author zhajianjun
 * @date 2026-02-17 19:51
 */
@Data
public class TiFFmpegProperty {

    /** FFmpeg路径 */
    private String path;
    /** 超时时间(秒) */
    private Long timeout;
    /** 默认线程数 */
    private Integer threads;
    /** 硬件加速类型(cuda/vulkan) */
    private String hardware;

}
