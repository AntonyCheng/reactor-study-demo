package top.sharehome.demo01datastream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
     * 1、基础数据流的示例，这是基础中的基础
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
                // 并发，在名为"sync"的执行器上开启三个线程执行
                //.parallel().runOn(Schedulers.newParallel("sync",3))
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
                // 流被处理完，无论结果如何都需要做的操作（对并发数据流无效）
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
     * 2、数据流的创建，以不同方式创建1-10的数据流
     */
    private static void create() {

        // 1、使用just创建数据流
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        just.subscribe();

        // 2、使用range创建数据流
        Flux<Integer> range = Flux.range(1, 10);
        range.subscribe();

        // 3、使用fromStream创建数据流
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Flux<Integer> fromStream = Flux.fromStream(integerStream);
        fromStream.subscribe();

        // 4、使用fromArray创建数据流
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Flux<Integer> fromArray = Flux.fromArray(ints);
        fromArray.subscribe();

        // 5、使用fromIterable创建数据流
        List<Integer> iterator = new ArrayList<Integer>() {
            {
                for (int i = 1; i < 11; i++) {
                    add(i);
                }
            }
        };
        Flux<Integer> fromIterable = Flux.fromIterable(iterator);
        fromIterable.subscribe();

        // 6、使用响应式流创建数据流
        Flux<Integer> from = Flux.from(Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        from.subscribe();

    }

    /**
     * 3、流的订阅
     */
    private static void subscribe() throws InterruptedException {

        // 创建一个流
        Flux<Integer> flux = Flux
                .range(1, 10)
                .map(data -> {
                    if (data == 8) {
                        data = data / 0; // data等于8时，流数据发生异常
                    }
                    System.out.println("map..." + data);
                    return data;
                });

        // 1、subscribe() 空参方法可以用来激活流，但是不进行任何操作，如果流没有设置异常处理机制并遇到异常，整个数据流会被终止；
        flux.subscribe();

        System.out.println();
        Thread.sleep(2000);

        // 2、subscribe(Consumer) 传入消费者，表示消费正常流数据，如果流没有设置异常处理机制并遇到异常，整个数据流会被终止；
        flux.subscribe(data -> System.out.println("consumer..." + data));

        System.out.println();
        Thread.sleep(2000);

        // 3、subscribe(Consumer,Error) 传入消费者和异常处理器，表示消费正常数据，如果流没有设置异常处理机制并遇到异常，会处理异常，但是整个数据流会被终止；
        flux.subscribe(data -> System.out.println("consumer..." + data) // 正常数据处理
                , error -> System.out.println("error..." + error)); // 异常数据处理

        System.out.println();
        Thread.sleep(2000);

        // 4、subscribe(Consumer,Error,Complete) 传入消费者、异常处理器和数据流全部消费成功处理器，如果流没有设置异常处理机制并遇到异常，会处理异常，但是整个数据流会被终止，进而不会执行到消费成功处理器；
        flux.subscribe(data -> System.out.println("consumer..." + data),
                error -> System.out.println("error..." + error),
                () -> System.out.println("success... done"));

        System.out.println("以上四种均能够调用flux.subscribe().isDisposed()查看流是否结束");
        Thread.sleep(2000);

        // 5、使用CoreSubscriber接口来进行消费，它和4中的方式大同小异，它需要在onSubscribe方法中手动开启订阅，当然这也代表它能在开启订阅时做一些操作，但是一般不用这个接口，
        //    如果流没有设置异常处理机制并遇到异常，会处理异常，无论异常之后是否开启订阅，整个数据流都会被终止
        flux.subscribe(new CoreSubscriber<Integer>() {

            // 先将绑定关系保存上
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("subscribe...");
                // 在开启订阅之前手动保存绑定关系
                this.subscription = s;
                // 手动开启首轮订阅，即：从数据流中取出第1/n个数据
                s.request(1);
            }

            @Override
            public void onNext(Integer data) {
                System.out.println("consumer..." + data);
                // 处理完之后使用绑定关系开启下一轮订阅
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable error) {
                System.out.println("error..." + error);
            }

            @Override
            public void onComplete() {
                System.out.println("complete... done");
            }
        });

        System.out.println();
        Thread.sleep(2000);

        // 6、使用BaseSubscriber接口来进行消费，这个算是对5的增强，一般情况下也采用这个写法，因为这里不需要开发者保存绑定关系，内置了对绑定关系的维护，
        //    而接口通常需要实现方法均为hookOnXXX的钩子函数，可实现“当开始订阅时”、“数据流订阅时”、“数据流订阅完成时”、“数据流订阅异常时”、“数据流订阅取消时”、“数据流最终执行程序”，
        //    如果流没有设置异常处理机制并遇到异常，会处理异常，无论异常之后是否开启订阅，整个数据流都会被终止，执行最终执行的代码。
        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("subscribe...");
                // 手动开启首轮订阅，即：从数据流中取出第1/n个数据
                request(1);
                // 从数据流取出所有数据，如果这里采用全部取出，那么在这个接口实现过程中，所有方法就不再需要使用request()开启下一轮订阅
                //requestUnbounded();
            }

            @Override
            protected void hookOnNext(Integer data) {
                System.out.println("consumer..." + data);
                if (data == 6){
                    // 当data等于6时，取消订阅
                    cancel();
                }
                // 再次开启下一轮订阅
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("complete... done");
            }

            @Override
            protected void hookOnError(Throwable error) {
                System.out.println("error..." + error);
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("cancel... done");
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("finally..." + type);
            }
        });

    }

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws Exception {
//        flux();
//        create();
        subscribe();
        System.in.read();
    }

}
