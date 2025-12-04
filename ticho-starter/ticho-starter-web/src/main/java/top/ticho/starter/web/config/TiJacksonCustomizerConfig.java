package top.ticho.starter.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.datatype.jsr310.JavaTimeModule;
import top.ticho.starter.web.converter.TiLocalDateDeserializer;
import top.ticho.starter.web.converter.TiLocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * RequestBody格式化
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Configuration
public class TiJacksonCustomizerConfig {
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String localDateTimePattern;

    @Value("${spring.jackson.local-date-format:yyyy-MM-dd}")
    private String localDatePattern;

    @Value("${spring.jackson.local-time-format:HH:mm:ss}")
    private String localTimePattern;

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> {
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(localDateTimePattern)));
            simpleModule.addDeserializer(LocalDateTime.class, new TiLocalDateTimeDeserializer(DateTimeFormatter.ofPattern(localDateTimePattern)));

            simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(localDatePattern)));
            simpleModule.addDeserializer(LocalDate.class, new TiLocalDateDeserializer(DateTimeFormatter.ofPattern(localDatePattern)));

            simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(localTimePattern)));
            simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(localTimePattern)));

            /*
             * Jackson全局转化long类型为String,解决jackson序列化时传入前端Long类型缺失精度问题
             */
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.addModule(new JavaTimeModule());
            builder.addModule(simpleModule);
        };
    }

}
