package top.sharehome.demo05sinks;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadFactory;

/**
 * Sinks工具类示例代码
 * sink是管道的意思，但是在Reactor中sink指的是中间过程
 * 在以前的学习中我们接触到sink的地方有三个方法：Flux.generate()、Flux.create()以及handle()，
 * 他们的作用绝大多数是往数据流中塞数据，而现在接触的Sinks工具类有更强大的功能。
 *
 * @author AntonyCheng
 */

public class SinksDemo {

    /**
     * 1、回顾一下以前对于sink的用法
     */
    private static void previousUsage() {
        // 1、generate同步创建1~10数据流
        Flux.generate(() -> 0, (data, sink) -> {
                    data += 1;
                    if (data == 11) {
                        sink.complete();
                    } else {
                        sink.next(data);
                    }
                    return data;
                })
                .log()
                .subscribe();
        // 2、create异步创建1~10数据流
        ThreadFactory factory = Thread.ofPlatform().factory();
        Flux.create(sink -> {
                    for (int i = 1; i <= 10; i++) {
                        int finalI = i;
                        factory.newThread(() -> {
                            sink.next(finalI);
                        }).start();
                    }
                })
                .log()
                .subscribe();
        // 3、handle中间过程处理
        Flux.range(1, 10)
                .handle((data, sink) -> {
                    if (data % 2 == 0) {
                        sink.next(data);
                    }
                })
                .log()
                .subscribe();
        System.out.println("previous usage done...\n");
    }

    /**
     * 2、使用Sinks工具类创建流
     * Sinks工具类中主要有五个概念，分别对应了五种方法：
     * 单播：只允许一个订阅者订阅，第二个订阅者订阅时会报错；                                               <==>  unicast()方法
     * 多播：允许多个订阅者订阅，但是只有第一个订阅者能够订阅完数据流的全部内容，之后的订阅者不能订阅之前发布的内容；  <==>  multicast()方法
     * 重放：当多个订阅者订阅时，允许非第一个订阅者订阅之前发布的内容；                                       <==>  replay()方法
     * 背压：在单播或者多播模式下，需要使用背压缓冲区进行缓冲（多播模式下也可以使用直接全部传输）                  <==>  onBackpressureBuffer()方法
     * 缓存：在发布者没有被dispose的情况下，可以设置缓存达到重放的效果（没有缓存则代表全部传输）                  <==>  cache()方法
     * 在接触上面的方法之前需要知道Sinks工具类创建一个数据流的过程，在此之后以举例子的方式来细说单播、多播、重放和缓存。
     */
    private static void createBySinks() {
        // 1、单播多元素数据流
        Flux<Object> unicastFlux = Sinks.many().unicast().onBackpressureBuffer().asFlux();
        // 2、多播多元素数据流
        Flux<Object> multicastFlux1 = Sinks.many().multicast().onBackpressureBuffer().asFlux();
        Flux<Object> multicastFlux2 = Sinks.many().multicast().directBestEffort().asFlux();
        Flux<Object> multicastFlux3 = Sinks.many().multicast().directAllOrNothing().asFlux();
        // 3、单播空数据流
        Mono<Object> mono1 = Sinks.one().asMono();
        Mono<Object> mono2 = Sinks.empty().asMono();
    }

    /**
     * 2.1、创建一个1~10数据流，且只想让一个订阅者进行订阅（单播）
     */
    private static void unicastSample() {
        // 首先创建单播多元素Sinks：
        Sinks.Many<Integer> unicastSinks = Sinks.many().unicast().onBackpressureBuffer();
        // 异步向Sinks里面输送元素
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                unicastSinks.tryEmitNext(i);
                // 为了看出效果，这里延迟一秒再输送
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        // 将Sinks转换成数据流
        Flux<Integer> unicast = unicastSinks.asFlux();
        // 第一个订阅者开始订阅
        unicast.subscribe(data -> {
            System.out.println("sub1-" + data);
        });
        // 第二个订阅者开始订阅
        unicast.subscribe(data -> {
            System.out.println("sub2-" + data);
        });
    }

    /**
     * 2.2、创建一个1~10数据流，让它能被两个订阅者订阅，但是后来者应该订阅到更少数据（无重放多播）
     */
    private static void multicastSample() throws InterruptedException {
        // 首先创建多播多元素Sinks：
        Sinks.Many<Integer> multicastSinks = Sinks.many().multicast().onBackpressureBuffer();
        // 异步向Sinks里面输送元素
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                multicastSinks.tryEmitNext(i);
                // 为了看出效果，这里延迟一秒再输送
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        // 将Sinks转换成数据流
        Flux<Integer> multicast = multicastSinks.asFlux();
        // 第一个订阅者开始订阅
        multicast.subscribe(data -> {
            System.out.println("sub1-" + data);
        });
        // 模拟第二个订阅者姗姗来迟
        Thread.sleep(5000);
        // 第二个订阅者开始订阅
        multicast.subscribe(data -> {
            System.out.println("sub2-" + data);
        });
    }

    /**
     * 2.3、创建一个1~10数据流，让它能被两个订阅者订阅，而且后来者能订阅到已经发布过的最近三个元素（可重放多播）
     */
    private static void replaySample() throws InterruptedException {
        // 首先创建可重放多元素Sinks：
        Sinks.Many<Integer> replaySinks = Sinks.many().replay().limit(3);
        // 异步向Sinks里面输送元素
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                replaySinks.tryEmitNext(i);
                // 为了看出效果，这里延迟一秒再输送
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        // 将Sinks转换成数据流
        Flux<Integer> multicast = replaySinks.asFlux();
        // 第一个订阅者开始订阅
        multicast.subscribe(data -> {
            System.out.println("sub1-" + data);
        });
        // 模拟第二个订阅者姗姗来迟
        Thread.sleep(5000);
        // 第二个订阅者开始订阅
        multicast.subscribe(data -> {
            System.out.println("sub2-" + data);
        });
    }

    /**
     * 2.4、创建一个1~10数据流，让它能被两个订阅者订阅，而且后来者能订阅到已经发布过的最近三个元素（缓存）
     */
    private static void cacheSample() throws InterruptedException {
        // 首先创建一个可缓存数据流
        Flux<Integer> cacheFlux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                // cache缓存的机制其实就是重放，如果没有缓存，那么默认为第二个订阅者能够从头开始取数据
                .cache(3);
        // 第一个订阅者对它进行订阅
        cacheFlux.subscribe(data->{
            System.out.println("sub1-"+data);
        });
        // 模拟第二个订阅者姗姗来迟
        Thread.sleep(5000);
        // 第二个订阅者对它进行订阅
        cacheFlux.subscribe(data->{
            System.out.println("sub2-"+data);
        });
    }

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException, InterruptedException {

//        previousUsage();
//        createBySinks();
//        unicastSample();
//        multicastSample();
//        replaySample();
        cacheSample();

        System.in.read();

    }

}
