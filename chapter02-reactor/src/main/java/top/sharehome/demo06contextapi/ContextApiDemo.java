package top.sharehome.demo06contextapi;

import reactor.core.publisher.Flux;
import reactor.util.context.Context;

/**
 * Context API示例代码
 * 在阻塞式编码中常用ThreadLocal进行线程中上下文信息的传递，但是在响应式编程中，线程切换频率极高，和线程深度绑定的ThreadLocal在这里就失效了，
 * 在Reactor中有能解决这个问题的方案，那就是Context API；
 * 这类API最大的特点就是从下游往上游进行数据传递，且对上游均可见，其次需要能够处理Context的API（方法名往往包含Contextual）做处理。
 *
 * @author AntonyCheng
 */

public class ContextApiDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) {

        // 现在有一个需求，那就是将1~10数据流每个数据加一个前后缀，而前后缀存放在Context中。
        Flux.range(1, 10)
                // 开始转换前缀
                .transformDeferredContextual((flux, context) -> {
                    return flux.map(data -> context.get("prefix") + "-" + data);
                })
                // 开始转换后缀
                .transformDeferredContextual((flux, context) -> {
                    return flux.map(data -> data + "-" + context.get("suffix"));
                })
                // 由于是从下往上传播，那么就需要编码于支持Context的API的下方（不需要紧挨着）
                // 后缀Context
                .contextWrite(Context.of("suffix", "ony"))
                // 前缀Context
                .contextWrite(Context.of("prefix", "ant"))
                .subscribe(System.out::println);

    }

}
