package top.ticho.tool.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.ticho.tool.core.TiNumberUtil;
import top.ticho.tool.core.TiStrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-11-08 09:40
 */
@Getter
@AllArgsConstructor
public enum YesOrNo {
    YES(1, "是"),
    NO(0, "否");
    private final int code;
    private final String value;

    private static final Map<Integer, YesOrNo> YES_OR_NO_MAP = new HashMap<>();

    static {
        for (YesOrNo yesOrNo : values()) {
            YES_OR_NO_MAP.put(yesOrNo.code, yesOrNo);
        }
    }

    public static YesOrNo getByCode(Integer code) {
        return YES_OR_NO_MAP.get(code);
    }

    public static String getValueByCode(Integer code) {
        YesOrNo yesOrNo = getByCode(code);
        return yesOrNo == null ? null : yesOrNo.getValue();
    }

    public static YesOrNo getByCode(Boolean code) {
        return code == null ? null : getByCode(code ? 1 : 0);
    }

    public static String getValueByCode(Boolean code) {
        YesOrNo yesOrNo = getByCode(code);
        return yesOrNo == null ? null : yesOrNo.getValue();
    }

    public static YesOrNo getByCode(String code) {
        if (code == null) {
            return null;
        }
        if (TiStrUtil.isNumber(code)) {
            return getByCode(TiNumberUtil.parseInt(code));
        }
        if (TiStrUtil.isBool(code) ) {
            return getByCode(Boolean.parseBoolean(code));
        }
        return null;
    }


    public static String getValueByCode(String code) {
        YesOrNo yesOrNo = getByCode(code);
        return yesOrNo == null ? null : yesOrNo.getValue();
    }


}
