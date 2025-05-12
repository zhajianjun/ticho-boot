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
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime反序列化
 * <p>
 * 主要处理 yyyy-MM-dd 转换 yyyy-MM-dd HH:mm:ss
 * </p>
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 * @see LocalDateDeserializer
 */
@SuppressWarnings("all")
public class TiLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DEFAULT_FORMATTER;
    public static final TiLocalDateTimeDeserializer INSTANCE;

    private TiLocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public TiLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    protected TiLocalDateTimeDeserializer(TiLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    protected TiLocalDateTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
        return new TiLocalDateTimeDeserializer(formatter);
    }

    protected TiLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new TiLocalDateTimeDeserializer(this, leniency);
    }

    protected TiLocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasTokenId(6)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return !this.isLenient() ? (LocalDateTime) this._failForNotLenient(parser, context, JsonToken.VALUE_STRING) : null;
            } else {
                try {
                    if (string.matches(TiDateFormatConst.YYYY_MM_DD_REGEX)) {
                        LocalDate localDate = LocalDate.parse(string, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD));
                        return LocalDateTime.of(localDate, LocalTime.MIN);
                    }
                    if (this._formatter == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
                        return string.endsWith("Z") ? LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC) : LocalDateTime.parse(string, DEFAULT_FORMATTER);
                    } else {
                        return LocalDateTime.parse(string, this._formatter);
                    }
                } catch (DateTimeException var12) {
                    return (LocalDateTime) this._handleDateTimeException(context, var12, string);
                }
            }
        } else {
            if (parser.isExpectedStartArrayToken()) {
                JsonToken t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return null;
                }

                LocalDateTime result;
                if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    result = this.deserialize(parser, context);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        this.handleMissingEndArrayForSingle(parser, context);
                    }

                    return result;
                }

                if (t == JsonToken.VALUE_NUMBER_INT) {
                    int year = parser.getIntValue();
                    int month = parser.nextIntValue(-1);
                    int day = parser.nextIntValue(-1);
                    int hour = parser.nextIntValue(-1);
                    int minute = parser.nextIntValue(-1);
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute);
                    } else {
                        int second = parser.getIntValue();
                        t = parser.nextToken();
                        if (t == JsonToken.END_ARRAY) {
                            result = LocalDateTime.of(year, month, day, hour, minute, second);
                        } else {
                            int partialSecond = parser.getIntValue();
                            if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                                partialSecond *= 1000000;
                            }

                            if (parser.nextToken() != JsonToken.END_ARRAY) {
                                throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                            }

                            result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                        }
                    }

                    return result;
                }

                context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", new Object[]{t});
            }

            if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                return (LocalDateTime) parser.getEmbeddedObject();
            } else {
                if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    this._throwNoNumericTimestampNeedTimeZone(parser, context);
                }

                return (LocalDateTime) this._handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
            }
        }
    }

    static {
        DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        INSTANCE = new TiLocalDateTimeDeserializer();
    }
}
