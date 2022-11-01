package com.ticho.boot.web.converter;

import cn.hutool.core.text.CharSequenceUtil;
import com.ticho.boot.json.DateFormatConsant;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate转换
 * <p>
 *     GET请求，Query查询Date时间类型参数转换
 * </p>
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Component
public class LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(@NonNull String source) {
        if (CharSequenceUtil.isBlank(source)) {
            return null;
        }
        if (source.matches(DateFormatConsant.YYYY_MM_DD_REGEX)) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD));
        }
        throw new IllegalArgumentException("Invalid value '" + source + "'");
    }
}