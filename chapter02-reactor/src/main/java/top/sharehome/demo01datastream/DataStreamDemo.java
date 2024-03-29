package top.sharehome.demo01datastream;

import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
     * 2、数据流的创建，以不同方式创建1-10的数据流，
     */
    private static void create() {

        // 1、使用just创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        just.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-just-" + data));

        // 2、使用range创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Flux<Integer> range = Flux.range(1, 10);
        range.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-range-" + data));

        // 3、使用fromStream创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Flux<Integer> fromStream = Flux.fromStream(integerStream);
        fromStream.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-fromStream-" + data));

        // 4、使用fromArray创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Flux<Integer> fromArray = Flux.fromArray(ints);
        fromArray.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-fromArray-" + data));

        // 5、使用fromIterable创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        List<Integer> iterator = new ArrayList<Integer>() {
            {
                for (int i = 1; i < 11; i++) {
                    add(i);
                }
            }
        };
        Flux<Integer> fromIterable = Flux.fromIterable(iterator);
        fromIterable.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-fromIterable-" + data));

        // 6、使用响应式流创建数据流（默认同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Flux<Integer> from = Flux.from(Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        from.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-from-" + data));

        // 7、使用generate创建数据流（自定义同步创建，可同步/异步操作，大白话：单线程向Flux中添加进而创建流，然后允许转换为异步数据流去执行）
        Flux<Integer> generate = Flux.generate(
                // 创建初始值，建议创建为数据流中数据类型的初始值
                () -> 0,
                (data, sink) -> {
                    // 迭代式处理数据
                    data += 1;
                    sink.next(data);
                    if (data == 10) {
                        sink.complete();
                    }
                    return data;
                }
        );
        generate.subscribe(data -> System.out.println(Thread.currentThread().getName() + "-generate-" + data));

        // 8、使用create创建数据流（自定义同步/异步创建，可同步/异步操作，大白话：可以选择n个线程向Flux中添加进而创建流，然后允许转换为同步/异步数据流去执行）
        // 创建线程工厂，为多线程多准备
        ThreadFactory virtualFactory = Thread.ofVirtual().factory();
        ThreadFactory platformFactory = Thread.ofPlatform().factory();
        Flux<Integer> create = Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                platformFactory.newThread(() -> {
                    if (finalI > 4) {
                        sink.next(finalI + 1);
                    }
                }).start();
                virtualFactory.newThread(() -> {
                    if (finalI <= 4) {
                        sink.next(finalI + 1);
                    }
                }).start();
            }
        });
        create.subscribe(data -> System.out.println((Thread.currentThread().isVirtual() ? "virtual-thread-" : Thread.currentThread().getName()) + "-create-" + data));

    }

    /**
     * 3、数据流的自定义处理，这里着重介绍handle的用法
     * 这个方法的优势就在于它将流的常用操作（map、flatMap等等）都能融入进来
     */
    private static void handle() {

        // 首先创建一个流
        Flux<Integer> generate = Flux.generate(
                () -> 0,
                (data, sink) -> {
                    data += 1;
                    if (data == 10) {
                        sink.complete();
                    }
                    sink.next(data);
                    return data;
                });

        // 然后对流进行处理
        Flux<List> handle = generate.handle((data, sink) -> {
            // 假设这里根据data从数据库查出一个List数据
            List<Integer> list = Collections.singletonList(data);
            System.out.println(list);
            // 转换之后直接将数据放入sink中，如果没有处理的数据就不需要执行sink.next()
            // 由于这里是同步Sink，所以只能调用一次sink.next方法
            sink.next(list);
        });

        // 最后对其进行订阅
        handle.subscribe(System.out::println);

    }

    /**
     * 4、数据流的线程模型和调度器（线程池）
     * 数据流的创建以及中间操作默认使用当前线程，在Reactor中多线程都需要额外维护调度器（线程池）
     */
    private static void thread() {

        // 1、了解一下Reactor中的常用调度器
        // （1）单线程池
        //      1）默认当前线程
        Scheduler immediate = Schedulers.immediate();
        //      2）另外开一个单线程
        Scheduler single1 = Schedulers.single();
        //      3）另外开一个名为single的单线程
        Scheduler single2 = Schedulers.newSingle("single");
        // （2）多线程池
        //      1）另外开启一个有界的线程池，默认参数为CPU核心数量10倍的workers，100k个队列，60秒空闲时间
        Scheduler boundedElastic1 = Schedulers.boundedElastic();
        //      2）另外开启一个有界的线程池，参数为10个workers，1k个队列，名称为“boundedElastic2”，60秒空闲时间
        Scheduler boundedElastic2 = Schedulers.newBoundedElastic(10, 1000, "boundedElastic2", 60);
        //      3）另外开启一个自定义的10-100线程，60秒空闲时间，有10k个队列的线程池
        Scheduler fromExecutor = Schedulers.fromExecutor(
                new ThreadPoolExecutor(
                        10,
                        100,
                        60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10000))
        );
        // （3）并发线程池（基于Fork-Join模型建立并发）
        //      1）开启一个线程数为CPU核心数的并发线程池
        Scheduler parallel1 = Schedulers.parallel();
        //      2）开启一个线程数为20的并发线程池，注意：线程数最大值即为CPU核心数，超出部分无效
        Scheduler parallel2 = Schedulers.newParallel("parallel", 20);

        // 2、了解一下publishOn(Scheduler)和subscribeOn(Scheduler)这两个设置线程调度策略的方法，它们主要强调线程池的概念。
        // 先将数据流处理代码分为三个阶段：
        // Flux.range()       ==>  数据流创建阶段（必要）
        //     .handle1()     ==>  数据流处理阶段1（非必要）
        //     .handle2()     ==>  数据流处理阶段2（非必要）
        //     .subscribe()   ==>  数据流订阅阶段（必要）
        // （1）数据流创建阶段一定是在当前线程进行的；
        // （2）publishOn(Scheduler)方法是将Scheduler调度器范围覆盖于publishOn()方法下的数据链（除自定义subscribe中OnHookSubscribe()和request()方法外）；
        // （3）subscribeOn(Scheduler)方法是将Scheduler调度器范围覆盖至全局；
        // （4）若两者一起使用，两者前后顺序不重要，但publishOn()方法的覆盖优先级大于subscribeOn()方法；
        // （5）通常而言subscribeOn()方法对于一个数据流使用一次即可，但是publishOn方法可以根据功能位置的需求多次使用，改变线程调度器；
        // 下面举一些例子：
        Flux.range(1, 2)  // main
                .handle((data,sink)->{})  // single2
                .handle((data,sink)->{})  // single2
                .publishOn(Schedulers.newSingle("single1"))
                .subscribeOn(Schedulers.newSingle("single2"))
                .subscribe();  // single1
        Flux.range(1, 2)  // main
                .handle((data,sink)->{})  // single2
                .publishOn(Schedulers.newSingle("single1"))
                .handle((data,sink)->{})  // single1
                .subscribeOn(Schedulers.newSingle("single2"))
                .subscribe();  // single1
        Flux.range(1, 2)  // main
                .publishOn(Schedulers.newSingle("single1"))
                .handle((data,sink)->{})  // single1
                .handle((data,sink)->{})  // single1
                .subscribeOn(Schedulers.newSingle("single2"))
                .subscribe();  // single1
        Flux.range(1, 2)  // main
                .publishOn(Schedulers.newSingle("single1"))
                .subscribeOn(Schedulers.newSingle("single2"))
                .handle((data,sink)->{})  // single1
                .handle((data,sink)->{})  // single1
                .subscribe();  // single1
        Flux.range(1, 2)  // main
                .handle((data,sink)->{})  // single2
                .handle((data,sink)->{})  // single2
                .subscribeOn(Schedulers.newSingle("single2"))
                .subscribe();  // single2
        Flux.range(1, 2)  // main
                .handle((data,sink)->{})  // main
                .publishOn(Schedulers.newSingle("single1"))
                .handle((data,sink)->{})  // single1
                .subscribe();  // single1

        // 3、了解一下parallel().runOn(Scheduler)设置线程调度策略的方法，它主要强调并发的概念。
        // 这个方法是让Flux快速拥有并发能力，一旦使用了parallel()方法，Flux类型就会转变为ParallelFlux类型，
        // ParallelFlux类型中有两个常用方法：
        // （1）runOn(Scheduler)方法：用来设置调度策略；
        // （2）sequential()方法：将并发状态转变为非并发状态，从ParallelFlux类型转变为Flux类型，但是改变后所在线程依旧是并发状态的线程，只是线程之间不能并发了而已；
        // 这种方法开启并发是最直接的，它的作用域覆盖于runOn()方法下的数据链，如果存在sequential()方法，那么作用域就在runOn()和sequential()之间，
        // 由于对数据流的操作是用ParallelFlux类型，一些Flux类型的方法就不再适用，所以不建议runOn()方法下的数据链在不存在sequential()方法的情况下和publishOn()或者subscribeOn()方法一起用。
        Flux.range(1,10)
                // 开启CPU核数的线程的并发状态
                .parallel().runOn(Schedulers.parallel()).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });
        Flux.range(1,10)
                // 开启2条线程的并发状态
                .parallel(2).runOn(Schedulers.parallel()).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });
        Flux.range(1,10)
                // 开启2条名为parallel的线程的并发状态
                .parallel(2).runOn(Schedulers.newParallel("parallel")).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });
        Flux.range(1,10)
                // 开启2条名为parallel的线程的并发状态
                .parallel().runOn(Schedulers.newParallel("parallel",2)).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });
        Flux.range(1,10)
                // 开启2条名为parallel的线程的并发状态
                .parallel(2).runOn(Schedulers.newParallel("parallel",4)).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });
        Flux.range(1,10)
                // 开启2条名为parallel的线程的并发状态
                .parallel(2).runOn(Schedulers.newParallel("parallel",1)).sequential()
                .subscribe(data->{
                    System.out.println(Thread.currentThread().getName());
                });

    }

    /**
     * 5、数据流的订阅
     * 在Reactor中一共有六种常用的订阅方法，前四种属于默认订阅，后两种属于自定义订阅。
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
                if (data == 6) {
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
     * 6、数据流的重塑
     * 在Reactor中有一些对上游数据流进行重塑的功能，主要是：批量操作和限流。
     */
    private static void reshape() {

        // 1、批量操作，在Reactor中批量操作就是新建缓冲区，数据不再是以单个数据为单位，而是单个缓冲区，即request(1)开启一轮订阅不再是取一个数据，而是取一个缓冲区的数据
        //    取出的缓冲区类型是一个ArrayList集合类型，需要注意批量操作仅仅是对流中的数据进行整合，并没有限制流的速度，不要理解成只要有缓存就会有效率变动，这里的缓存主要作用是整合数据。
        // 创建一个批量操作演示数据流
        Flux<Integer> bufferFlux = Flux.range(1, 10);
        bufferFlux
                // 在此打印日志是为了查看数据流被操作的情况
                .log()
                // 没有参数的缓冲区大小默认为最大大小（Integer.MAX_VALUE），即没有缓冲区
                .buffer()
                .subscribe(System.out::println);
        bufferFlux
                // 在此打印日志是为了查看数据流被操作的情况
                .log()
                // 设置参数之后就以参数作为缓冲区大小，即每轮订阅取出3个数据组成1个ArrayList集合进行操作
                .buffer(3)
                .subscribe(System.out::println);

        // 2、限流操作，对于RabbitMQ消息队列的限流而言就是设置一个阈值，消费者每次从队列中取出小于等于阈值的个数，其实在Reactor中限流原理大同小异
        //    在Reactor中限流限制的就是订阅者request请求轮数，并且按照75%阈值进行订阅，假设有200个数据流，设定限流40个，request情况就是：40->30->30->30->30->30->30，
        //    即第一次取出限制个数，之后每当订阅完限制个数的75%时，就再取出限制个数的75%，直到流被订阅完为止，这个限流是对发布者的数据流进行设置的，所以订阅者中request操作对整个过程无效。
        Flux.range(1, 1000)
                // 在此打印日志是为了查看数据流被操作的情况
                .log()
                // 限制流量
                .limitRate(100)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        // 不影响限流操作
                        request(20);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        // 不影响限流操作
                        request(200);
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
//        handle();
//        thread();
//        subscribe();
        reshape();
        System.in.read();
    }

}
