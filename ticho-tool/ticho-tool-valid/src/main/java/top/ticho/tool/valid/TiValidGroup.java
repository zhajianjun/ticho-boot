package top.ticho.tool.valid;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

/**
 * 校验分组
 *
 * @author zhajianjun
 * @date 2025-10-19 13:51
 */
public class TiValidGroup {

    public interface Add {
    }

    public interface Upd {
    }

    @GroupSequence({Default.class, Add.class, Upd.class})
    public interface CheckSequence {
    }

}
