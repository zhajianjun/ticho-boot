package top.ticho.starter.web.converter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import top.ticho.tool.json.constant.TiDateFormatConst;

import java.io.IOException;
import java.io.Serial;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime反序列化
 * <p>
 * 主要处理 yyyy-MM-dd HH:mm:ss 转换 yyyy-MM-dd
 * </p>
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 * @see LocalDateDeserializer
 */
@SuppressWarnings("all")
public class TiLocalDateDeserializer extends JSR310DateTimeDeserializerBase<LocalDate> {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static final TiLocalDateDeserializer INSTANCE = new TiLocalDateDeserializer();

    protected TiLocalDateDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public TiLocalDateDeserializer(DateTimeFormatter dtf) {
        super(LocalDate.class, dtf);
    }

    /**
     * Since 2.10
     */
    public TiLocalDateDeserializer(TiLocalDateDeserializer base, DateTimeFormatter dtf) {
        super(base, dtf);
    }

    /**
     * Since 2.10
     */
    protected TiLocalDateDeserializer(TiLocalDateDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /**
     * Since 2.11
     */
    protected TiLocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape) {
        super(base, shape);
    }

    @Override
    protected TiLocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new TiLocalDateDeserializer(this, dtf);
    }

    @Override
    protected TiLocalDateDeserializer withLeniency(Boolean leniency) {
        return new TiLocalDateDeserializer(this, leniency);
    }

    @Override
    protected TiLocalDateDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                if (!isLenient()) {
                    return _failForNotLenient(parser, context, JsonToken.VALUE_STRING);
                }
                return null;
            }
            // as per [datatype-jsr310#37], only check for optional (and, incorrect...) time marker 'T'
            // if we are using default formatter
            DateTimeFormatter format = _formatter;
            try {
                // 添加的内容在这
                if (string.matches(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_REGEX)) {
                    LocalDateTime localDateTime = LocalDateTime.parse(string, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS));
                    return localDateTime.toLocalDate();
                }
                // 添加的内容在这
                if (string.matches(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_SSS_REGEX)) {
                    LocalDateTime localDateTime = LocalDateTime.parse(string, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS_SSS));
                    return localDateTime.toLocalDate();
                }
                if (_formatter == DEFAULT_FORMATTER) {
                    // JavaScript by default includes time in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length() > 10 && string.charAt(10) == 'T') {
                        if (string.endsWith("Z")) {
                            return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC).toLocalDate();
                        } else {
                            return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                    }
                }
                return LocalDate.parse(string, format);
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
                final LocalDate parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);

                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    throw context.wrongTokenException(
                        parser, handledType(), JsonToken.END_ARRAY,
                        "Expected array to end"
                    );
                }
                return LocalDate.of(year, month, day);
            }
            context.reportInputMismatch(
                handledType(),
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                t
            );
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDate) parser.getEmbeddedObject();
        }
        // 06-Jan-2018, tatu: Is this actually safe? Do users expect such coercion?
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            // issue 58 - also check for NUMBER_INT, which needs to be specified when serializing.
            if (_shape == JsonFormat.Shape.NUMBER_INT || isLenient()) {
                return LocalDate.ofEpochDay(parser.getLongValue());
            }
            return _failForNotLenient(parser, context, JsonToken.VALUE_STRING);
        }
        return _handleUnexpectedToken(context, parser, "Expected array or string.");
    }
}
