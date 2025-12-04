package top.ticho.starter.web.converter;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.JSR310DateTimeDeserializerBase;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import top.ticho.tool.core.constant.TiDateFormatConst;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final TiLocalDateTimeDeserializer INSTANCE = new TiLocalDateTimeDeserializer();

    /**
     * Flag for <code>JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS</code>
     */
    protected final Boolean _readTimestampsAsNanosOverride;

    protected TiLocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public TiLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
        _readTimestampsAsNanosOverride = null;
    }

    protected TiLocalDateTimeDeserializer(TiLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
        _readTimestampsAsNanosOverride = base._readTimestampsAsNanosOverride;
    }

    protected TiLocalDateTimeDeserializer(TiLocalDateTimeDeserializer base,
                                          Boolean leniency,
                                          DateTimeFormatter formatter,
                                          JsonFormat.Shape shape,
                                          Boolean readTimestampsAsNanosOverride) {
        super(base, leniency, formatter, shape);
        _readTimestampsAsNanosOverride = readTimestampsAsNanosOverride;
    }

    @Override
    protected TiLocalDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new TiLocalDateTimeDeserializer(this, _isLenient, dtf, _shape, _readTimestampsAsNanosOverride);
    }

    @Override
    protected TiLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new TiLocalDateTimeDeserializer(this, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt,
                                                                     BeanProperty property, JsonFormat.Value formatOverrides) {
        TiLocalDateTimeDeserializer deser = (TiLocalDateTimeDeserializer)
            super._withFormatOverrides(ctxt, property, formatOverrides);
        Boolean readTimestampsAsNanosOverride = formatOverrides.getFeature(
            JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        if (!Objects.equals(readTimestampsAsNanosOverride, deser._readTimestampsAsNanosOverride)) {
            return new TiLocalDateTimeDeserializer(deser, deser._isLenient, deser._formatter,
                deser._shape, readTimestampsAsNanosOverride);
        }
        return deser;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException {
        if (p.hasTokenId(JsonTokenId.ID_STRING)) {
            return _fromString(p, ctxt, p.getString());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            final String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
        } else if (p.isExpectedStartArrayToken()) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final LocalDateTime parsed = deserialize(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;

                int year = p.getIntValue();
                int month = p.nextIntValue(-1);
                int day = p.nextIntValue(-1);
                int hour = p.nextIntValue(-1);
                int minute = p.nextIntValue(-1);

                t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = p.getIntValue();
                    t = p.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = p.getIntValue();
                        if (partialSecond < 1_000 && !shouldReadTimestampsAsNanoseconds(ctxt))
                            partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY,
                                "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            ctxt.reportInputMismatch(handledType(),
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                t);
        } else if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) p.getEmbeddedObject();
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(p, ctxt);
        }
        return _handleUnexpectedToken(ctxt, p, "Expected array or string");
    }

    protected boolean shouldReadTimestampsAsNanoseconds(DeserializationContext context) {
        return (_readTimestampsAsNanosOverride != null) ? _readTimestampsAsNanosOverride :
            context.isEnabled(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    protected LocalDateTime _fromString(JsonParser p, DeserializationContext ctxt, String string0)
        throws JacksonException {
        String string = string0.trim();
        if (string.length() == 0) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(p, ctxt, string);
        }
        final DateTimeFormatter format = _formatter;
        try {
            // 21-Oct-2020, tatu: Changed as per [modules-base#94] for 2.12,
            //    had bad timezone handle change from [modules-base#56]
            if (_formatter == DEFAULT_FORMATTER) {
                // 添加的内容在这
                if (string.matches(TiDateFormatConst.YYYY_MM_DD_REGEX)) {
                    LocalDate localDate = LocalDate.parse(string, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD));
                    return LocalDateTime.of(localDate, LocalTime.MIN);
                }
                // ... only allow iff lenient mode enabled since
                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                if (string.length() > 10 && string.charAt(10) == 'T') {
                    if (string.endsWith("Z")) {
                        if (isLenient()) {
                            if (ctxt.isEnabled(DateTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)) {
                                return Instant.parse(string).atZone(ctxt.getTimeZone().toZoneId()).toLocalDateTime();
                            }
                            return LocalDateTime.parse(string.substring(0, string.length() - 1),
                                _formatter);
                        }
                        JavaType t = getValueType(ctxt);
                        return (LocalDateTime) ctxt.handleWeirdStringValue(t.getRawClass(),
                            string,
                            "Should not contain offset when 'strict' mode set for property or type (enable 'lenient' handling to allow)"
                        );
                    }
                }
            }
            return LocalDateTime.parse(string, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeFormatException(ctxt, e, format, string);
        }
    }

}
