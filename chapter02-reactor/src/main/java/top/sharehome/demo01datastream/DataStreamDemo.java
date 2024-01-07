package top.sharehome.demo01datastream;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

/**
 * 数据流示例代码
 * 再Reactor框架中有两种数据流，数据流由N个数据和1个信号（完成/异常信号）构成；
 * 1、Mono数据流：表示0/1个数据元素+1个信号（SignalType类中的信号）；
 * 2、Flux数据流：表示N个数据元素+1个信号（SignalType类中的信号）；
 * 这两种数据流在Reactor框架中都有对应的同名类，比较常用的方法如下：
 * 1、just => 创建多元素数据流（静态方法）
 * 2、empty => 创建空数据流（静态方法）
 * 3、interval => 创建定时递增数据流（静态方法）
 * 4、doOnXXX => XXX表示类中的一系列回调函数（也叫勾子函数，基于事件机制），在编码式尽量使用链式调用，并且
 * 5、subscribe => 从数据流中订阅数据
 * 使用API时需要学会看弹珠图，这个弹珠图可以直接在IDEA中将鼠标移动到方法上，能够出提示，如果下载了Reactor源码，那么就能看到每个方法的效果弹珠图；
 *
 * @author AntonyCheng
 */

public class DataStreamDemo {

    /**
     * 以Flux类型数据流为例，Mono类型数据流方法与其一致
     */
    private static void flux() {
        // 1、创建多元素（0~N）数据流
        Flux<Integer> dataStream = Flux.just(1, 2, 3, 4, 5, 0);
        // 在subscribe之前都是对发布者发布的数据流进行处理，之后都是对订阅者接收数据流进行的处理
        dataStream
                // 对数据流进行异常设计
                .map(data -> {
                    int i = 10 / data;
                    return data;
                })
                // 当流被开始订阅时
                .doOnSubscribe(subscription -> System.out.println("流被开始订阅时：" + subscription))
                // 当数据流到达时对数据中的数据元素做处理
                .doOnNext(num -> System.out.println("数据流中的数据元素为：" + num))
                // 打印上一步操作的日志，这里的上一步就是doOnNext
                .log()
                // 当数据流到达时对数据（包括数据元素和信号）做处理
                .doOnEach(integerSignal -> System.out.println("数据流中的数据为：" + integerSignal))
                // 当数据流发生异常时
                .doOnError(throwable -> System.out.println("数据流异常：" + throwable))
                // 当数据流完成处理时
                .doOnComplete(() -> System.out.println("数据流完成"))
                // 当流被取消时
                .doOnCancel(() -> System.out.println("数据流取消"))
                // 流被处理完，无论结果如何都需要做的操作
                .doFinally(signalType -> System.out.println("数据流结束于：" + signalType))
                // 订阅者订阅数据的操作
                .subscribe(
                        // 当数据流到达时，订阅者的行为
                        data -> System.out.println("订阅者得到的数据：" + data),
                        // 当数据流发生异常时，订阅者的行为
                        throwable -> System.out.println("订阅者得到的数据异常：" + throwable),
                        // 当数据流完成订阅时，订阅者的行为
                        () -> System.out.println("订阅者完成订阅")
                ).dispose();

        // 订阅多元素数据流
        dataStream.subscribe(e -> System.out.println("e1->" + e));
        dataStream.subscribe(e -> System.out.println("e2->" + e));

        // 2、创建空数据流（不包含数据，只包含完成信号）
        Flux<Object> emptyStream = Flux.empty();
        // 订阅空数据流
        emptyStream.subscribe(System.out::println);

        // 3、创建定时递增数据流，该数据流按照单位时间从0开始递增发布长整型数据
        Flux<Long> intervalStream = Flux.interval(Duration.ofSeconds(1));
        // 订阅定时数据流
        intervalStream.subscribe(System.out::println);
    }

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException {
        flux();

        while (true) {

        }
    }

}
