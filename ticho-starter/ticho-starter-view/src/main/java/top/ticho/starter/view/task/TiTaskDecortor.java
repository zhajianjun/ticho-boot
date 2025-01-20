package top.ticho.starter.view.task;

import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 上下文传递数据装饰器
 * 该类用于在任务执行的不同阶段对数据进行装饰和处理，包括获取数据、传递数据和完成任务时的数据处理
 * 主要用于任务执行链条中，对同一个数据对象在不同阶段进行操作的场景
 *
 * @author zhajianjun
 * @date 2023-01-05 10:53
 */
@Data
public class TiTaskDecortor<T> {

    /** 数据 */
    private T data;
    /** 获取上文数据逻辑 */
    private Supplier<T> supplier;
    /** 传递到下文数据逻辑 */
    private Consumer<T> execute;
    /** 最后的处理逻辑 */
    private Consumer<T> complete;

    /**
     * 上下文数据填充
     * 通过调用supplier的get方法来获取数据，并将其赋值给data字段
     * 这是任务执行链的第一步，确保后续操作有正确的数据基础
     */
    public void setData() {
        this.data = supplier.get();
    }

    /**
     * 上下文数据传递
     * 如果data不为空，则调用execute方法处理data
     * 这一步是在任务执行链中传递数据，进行必要的处理或转发
     */
    public void execute() {
        if (data == null) {
            return;
        }
        execute.accept(data);
    }

    /**
     * 最后处理上下文数据
     * 如果data不为空，则调用complete方法处理data
     * 这是任务执行链的最后一步，用于完成任何必要的清理或最终处理
     */
    public void complete() {
        if (data == null) {
            return;
        }
        complete.accept(data);
    }

}
