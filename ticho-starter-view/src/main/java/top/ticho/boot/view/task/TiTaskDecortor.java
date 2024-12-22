package top.ticho.boot.view.task;

import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 上下文传递数据 对象
 *
 * @author zhajianjun
 * @date 2023-01-05 10:53
 */
@Data
public class TiTaskDecortor<T> {

    private T data;

    private Supplier<T> supplier;

    private Consumer<T> execute;

    private Consumer<T> complete;

    public void setData() {
        this.data = supplier.get();
    }

    public void execute() {
        if (data == null) {
            return;
        }
        execute.accept(data);
    }

    public void complete() {
        if (data == null) {
            return;
        }
        complete.accept(data);
    }

}

