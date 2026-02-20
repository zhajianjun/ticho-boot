package top.ticho.tool.ffmpeg;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * FFmpeg工具类
 *
 * @author zhajianjun
 * @date 2026-02-19 22:45
 */
@Slf4j
public class TiFFmpegUtil {

    // FFmpeg 可执行文件的路径，建议配置在配置文件中，这里为了演示写死
    private static final String FFMPEG_PATH = "ffmpeg";
    // 也可以配置 FFprobe 路径
    private static final String FFPROBE_PATH = "ffprobe";
    // 用于存储正在进行的任务，key为taskId
    private static final ConcurrentHashMap<String, Process> TASK_MAP = new ConcurrentHashMap<>();

    /**
     * 构建基础的命令列表
     */
    private static List<String> buildBaseCommand() {
        List<String> command = new ArrayList<>();
        command.add(FFMPEG_PATH);
        return command;
    }

    /**
     * 执行 FFmpeg 命令（同步阻塞方式）
     *
     * @param command 命令列表
     * @return 执行结果 (0表示成功)
     */
    public static int executeSync(List<String> command) {
        try {
            log.info("执行命令: {}", String.join(" ", command));
            ProcessBuilder builder = new ProcessBuilder(command);
            // 合并错误流和输出流
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 消耗输出流，防止缓冲区填满导致进程挂起
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[FFmpeg Log]: {}", line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("FFmpeg 任务执行成功");
            } else {
                log.error("FFmpeg 任务执行失败，退出码: {}", exitCode);
            }
            return exitCode;
        } catch (Exception e) {
            log.error("FFmpeg 执行异常", e);
            return -1;
        }
    }

    /**
     * 执行 FFmpeg 命令（异步方式，可回调进度）
     *
     * @param command  命令列表
     * @param consumer 日志行消费者（可用于解析进度）
     * @param taskId   任务唯一标识
     * @return CompletableFuture
     */
    public static CompletableFuture<Integer> executeAsync(List<String> command, Consumer<String> consumer, String taskId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Task [{}] 开始执行: {}", taskId, String.join(" ", command));
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.redirectErrorStream(true);
                Process process = builder.start();

                // 将进程放入Map，以便可以强制停止
                TASK_MAP.put(taskId, process);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (consumer != null) {
                            consumer.accept(line);
                        }
                    }
                }

                int exitCode = process.waitFor();
                TASK_MAP.remove(taskId);
                return exitCode;
            } catch (Exception e) {
                log.error("Task [{}] 执行异常", taskId, e);
                TASK_MAP.remove(taskId);
                return -1;
            }
        });
    }

    /**
     * 停止指定任务
     */
    public static boolean stopTask(String taskId) {
        Process process = TASK_MAP.get(taskId);
        if (process != null && process.isAlive()) {
            process.destroy();
            log.info("Task [{}] 已停止", taskId);
            return true;
        }
        return false;
    }

    // ========================= 业务功能封装 =========================

    /**
     * 视频转码/压缩
     *
     * @param inputPath  源文件路径
     * @param outputPath 输出文件路径
     * @param crf        压缩质量 (0-51, 越小质量越好，通常18-28)
     * @param preset     编码预设
     * @return 任务ID
     */
    public static String transcodeVideo(String inputPath, String outputPath, int crf, String preset) {
        String taskId = UUID.randomUUID().toString();
        List<String> command = buildBaseCommand();
        command.add("-i");
        command.add(inputPath);
        command.add("-c:v");
        command.add("libx264"); // 视频编码器
        command.add("-preset");
        command.add(preset);
        command.add("-crf");
        command.add(String.valueOf(crf));
        command.add("-c:a");
        command.add("aac"); // 音频编码器
        command.add("-strict");
        command.add("experimental");
        command.add("-y"); // 覆盖输出文件
        command.add(outputPath);
        // 异步执行，并简单打印日志
        executeAsync(command, log::info, taskId);
        return taskId;
    }

    /**
     * 视频截图
     *
     * @param inputPath  源视频路径
     * @param outputPath 图片输出路径
     * @param time       截图时间点 (格式: 00:00:05 或 秒数)
     */
    public static boolean captureScreenshot(String inputPath, String outputPath, String time) {
        List<String> command = buildBaseCommand();
        command.add("-ss");
        command.add(time);
        command.add("-i");
        command.add(inputPath);
        command.add("-vframes");
        command.add("1"); // 截取1帧
        command.add("-q:v");
        command.add("2"); // 图片质量
        command.add("-y");
        command.add(outputPath);

        return executeSync(command) == 0;
    }

    /**
     * 提取音频
     *
     * @param inputPath  源视频路径
     * @param outputPath 音频输出路径 (如 .mp3, .aac)
     */
    public static boolean extractAudio(String inputPath, String outputPath) {
        List<String> command = buildBaseCommand();
        command.add("-i");
        command.add(inputPath);
        command.add("-vn"); // 不处理视频
        command.add("-acodec");
        command.add("copy"); // 直接复制音频流，不重编码（如果格式支持）
        // 如果需要转码为mp3，可以使用: command.add("libmp3lame");
        command.add("-y");
        command.add(outputPath);

        return executeSync(command) == 0;
    }

    /**
     * 视频拼接 (使用 concat 协议)
     * 注意：需要先创建一个 list.txt 文件，内容为 "file 'video1.mp4'\nfile 'video2.mp4'"
     *
     * @param listFilePath 文件列表路径
     * @param outputPath   输出路径
     */
    public static boolean concatVideos(String listFilePath, String outputPath) {
        List<String> command = buildBaseCommand();
        command.add("-f");
        command.add("concat");
        command.add("-safe");
        command.add("0");
        command.add("-i");
        command.add(listFilePath);
        command.add("-c");
        command.add("copy"); // 直接流复制，速度快
        command.add("-y");
        command.add(outputPath);

        return executeSync(command) == 0;
    }

    /**
     * 添加水印
     *
     * @param inputPath  源视频
     * @param watermark  水印图片
     * @param outputPath 输出路径
     * @param position   位置 (overlay参数，例如 "10:10" 左上角, "main_w-overlay_w-10:10" 右上角)
     */
    public static String addWatermark(String inputPath, String watermark, String outputPath, String position) {
        String taskId = UUID.randomUUID().toString();
        List<String> command = buildBaseCommand();
        command.add("-i");
        command.add(inputPath);
        command.add("-i");
        command.add(watermark);
        command.add("-filter_complex");
        command.add("overlay=" + position);
        command.add("-y");
        command.add(outputPath);
        executeAsync(command, log::info, taskId);
        return taskId;
    }

}
