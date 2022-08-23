package com.ticho.boot.web.converter;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime转换
 * <p>
 *     GET请求，Query查询Date时间类型参数转换
 * </p>
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(@NonNull String source) {
        if (CharSequenceUtil.isBlank(source)) {
            return null;
        }
        if (source.matches(DateFormatConsant.YYYY_MM_DD_REGEX)) {
            LocalDate localDate = LocalDate.parse(source, DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD));
            return LocalDateTime.of(localDate, LocalTime.MIN);
        }
        if (source.matches(DateFormatConsant.YYYY_MM_DD_HH_MM_SS_REGEX)) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD_HH_MM_SS));
        }
        throw new IllegalArgumentException("Invalid value '" + source + "'");
    }
}
