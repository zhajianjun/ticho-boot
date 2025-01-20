package top.ticho.boot.web.util.valid;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * 校验分组
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
public class TiValidGroup {

    public interface Add {}

    public interface Upd {}

    @GroupSequence({Default.class, Add.class, Upd.class})
    public interface CheckSequence {}

}
