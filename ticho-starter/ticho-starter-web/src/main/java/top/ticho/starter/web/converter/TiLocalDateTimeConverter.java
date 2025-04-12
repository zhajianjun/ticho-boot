package top.ticho.starter.web.converter;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import top.ticho.tool.json.constant.TiDateFormatConst;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime转换
 * <p>
 * GET请求，Query查询Date时间类型参数转换
 * </p>
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Component
public class TiLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(@NonNull String source) {
        if (CharSequenceUtil.isBlank(source)) {
            return null;
        }
        if (source.matches(TiDateFormatConst.YYYY_MM_DD_REGEX)) {
            LocalDate localDate = LocalDate.parse(source, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD));
            return LocalDateTime.of(localDate, LocalTime.MIN);
        }
        if (source.matches(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_REGEX)) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS));
        }
        if (source.matches(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_SSS_REGEX)) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_SSS));
        }
        throw new IllegalArgumentException("Invalid value '" + source + "'");
    }
}
