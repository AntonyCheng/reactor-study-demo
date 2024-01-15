package top.sharehome.demo02apis;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 常规方法示例代码
 * 在这里会介绍一些Reactor中常用到的一些方法：
 * filter、flatMap、concatMap、flatMapMany、transform、defaultIfEmpty、switchIfEmpty、concat、
 * concatWith、merge、mergeWith、mergeSequential、zip、zipWith、timeout、retry
 *
 * @author AntonyCheng
 */
public class CommonApisDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException {

        /*
        filter 过滤
         */
        Flux.range(1, 10)
                // 挑选出偶数
                .filter(data -> data % 2 == 0)
                .log()
                .subscribe();

        System.out.println("Filter... Done\n");

        /*
        filter 过滤
         */
        Flux.just("Emma Johnson", "Benjamin Miller", "Sophia Davis")
                // 将姓名分隔开之后再放入数据流中
                .flatMap(data -> {
                    String[] s = data.split(" ");
                    return Flux.fromArray(s);
                })
                .log()
                .subscribe();

        System.out.println("FlatMap... Done\n");

        /*
        1、concatMap 同步连接多个不同/相同元素类型的数据流
        2、concatWith 同步连接一个元素类型相同的数据流
        相比于使用Flux.concat()方法，该两种方法更具有灵活性
         */
        Flux.just(Flux.just(1, 2), Flux.just("3", "4"), Flux.just('5', '6'))
                .concatMap(flux -> {
                    return flux.map(data -> {
                        return "flux-" + data;
                    });
                })
                .log()
                .subscribe();
        Flux.just(1, 2)
                .concatWith(Flux.just(3, 4))
                .log()
                .subscribe();
        Flux.concat(Flux.just(1, 2), Flux.just("3", "4"), Flux.just('5', '6'))
                .log()
                .subscribe();

        System.out.println("concatMap&concatWith... Done\n");

        /*
        1、mergeWith 异步合并一个元素类型相同的数据流
           相比于使用Flux.merge()方法，该种方法更具有灵活性
        2、Flux.mergeSequential 将异步发布的流按照首发元素时间排序后以数据流为单位同步发布出来
         */
        Flux.just(1, 2)
                .delayElements(Duration.ofMillis(1000))
                .mergeWith(Flux.just(4, 5).delayElements(Duration.ofMillis(800)))
                .subscribe(data -> System.out.println("flux mergeWith data: " + data));
        Flux.merge(
                        Flux.just(1, 2).delayElements(Duration.ofMillis(1000)),
                        Flux.just("3", "4").delayElements(Duration.ofMillis(800)),
                        Flux.just('5', '6').delayElements(Duration.ofMillis(600))
                )
                .subscribe(data -> System.out.println("flux merge data: " + data));
        Flux<? extends Flux<? extends Serializable>> fluxes = Flux.just(
                Flux.just(1, 2).delayElements(Duration.ofMillis(1000)),
                Flux.just("3", "4").delayElements(Duration.ofMillis(800)),
                Flux.just('5', '6').delayElements(Duration.ofMillis(600))
        );
        Flux.mergeSequential(fluxes)
                .subscribe(data -> System.out.println("flux mergeSequential data: " + data));

        System.out.println("mergeWith&mergeSequential... Done\n");

        /*
        1、transform 无状态转换数据流中的元素，不共享外部变量
        2、transformDeferred 有状态转换流中的元素，能共享外部变量
        流数据流的操作外部数据，为了保证线程安全，除了锁机制以外，还能够使用原子类
         */
        AtomicInteger atomicForTransform = new AtomicInteger(0);
        Flux<String> transform = Flux.just("a", "b", "c")
                .transform(stringFlux -> {
                    // ++atomicForTransform
                    int i = atomicForTransform.incrementAndGet();
                    // 判断原子类是否自增过一次
                    if (i == 1) {
                        // 不是 则返回全大写数据流
                        return stringFlux.map(String::toUpperCase);
                    } else {
                        // 是 则返回原小写数据流
                        return stringFlux;
                    }
                });
        // 以下两个打印出来的结果一样，由于没有共享原子类，所以每个订阅者读取都是没有自增的状态
        transform.subscribe(data -> {
            System.out.println("subscribe1 transform data: " + data);
        });
        transform.subscribe(data -> {
            System.out.println("subscribe2 transform data: " + data);
        });
        // 接下来是transformDeferred，首先创建原子类
        AtomicInteger atomicForTransformDeferred = new AtomicInteger();
        Flux<String> transformDeferred = Flux.just("a", "b", "c")
                .transformDeferred(stringFlux -> {
                    // ++atomicForTransformDeferred
                    int i = atomicForTransformDeferred.incrementAndGet();
                    // 判断原子类是否自增过一次
                    if (i == 1) {
                        // 不是 则返回全大写数据流
                        return stringFlux.map(String::toUpperCase);
                    } else {
                        // 是 则返回原小写数据流
                        return stringFlux;
                    }
                });
        // 以下两个打印出来的结果不一样，由于共了享原子类，第一次订阅已经使其自增，第二次订阅就会有不同的结果
        transformDeferred.subscribe(data -> {
            System.out.println("subscribe1 transformDeferred data: " + data);
        });
        transformDeferred.subscribe(data -> {
            System.out.println("subscribe2 transformDeferred data: " + data);
        });

        System.out.println("transform&transformDeferred... Done\n");

        /*
        1、defaultIfEmpty 如果发布者元素没有元素，指定静态默认值
        2、switchIfEmpty 如果发布者元素没有数据，指定动态默认值
        注意：如果发布null也是有元素的，但是底层是数组，遇到null会报错，所以空元素数据流都是用Flux.empty()或者Mono.empty()进行创建
         */
        Flux.empty()
                .defaultIfEmpty(0)
                .subscribe(data -> {
                    System.out.println("flux defaultIfEmpty data: " + data);
                });
        Mono.empty()
                .defaultIfEmpty(0)
                .subscribe(data -> {
                    System.out.println("mono defaultIfEmpty data: " + data);
                });
        Flux.empty()
                .switchIfEmpty(Flux.just(new Random().nextInt()))
                .subscribe(data -> {
                    System.out.println("flux switchIfEmpty data: " + data);
                });
        Mono.empty()
                .switchIfEmpty(Mono.just(new Random().nextInt()))
                .subscribe(data -> {
                    System.out.println("mono switchIfEmpty data: " + data);
                });

        System.out.println("defaultIfEmpty&switchIfEmpty... Done\n");

        /*
        1、zip 将n（最大值为8）个流压缩成包含元素数量最小流个数的n元组流
        例如：
        flux1 ==> 1 2 3 4 5
        flux2 ==> 2 5 6 7
        flux3 ==> 3 8
        zip的最终结果是：[1,2,3] [2,5,8]
        2、zipWith 将当前流和另一个流压缩成包含元素数量最小流个数的2元组流
        例如：
        current ==> 1 2 3 4 5
        once    ==> 2 5 6 7   ==> result:[1,2] [2,5] [3,6] [4,7]
        again   ==> 3 8       ==> result:[[1,2],3] [[2,5],8]
        zipWith的最终结果是：[[1,2],3] [[2,5],8]
         */
        Flux.zip(
                        Flux.just(1, 2, 3, 4, 5),
                        Flux.just("2", "5", "6", "7"),
                        Flux.just('3', '8')
                )
                .log()
                .map(tuple3 -> {
                    return tuple3.getT1() + "-" + tuple3.getT2() + "-" + tuple3.getT3();
                })
                .log()
                .subscribe();
        Flux.just(1, 2, 3, 4, 5)
                .zipWith(Flux.just("2", "5", "6", "7"))
                .zipWith(Flux.just('3', '8'))
                .log()
                .map(tuple3 -> {
                    return tuple3.getT1().getT1() + "-" + tuple3.getT1().getT2() + "-" + tuple3.getT2();
                })
                .log()
                .subscribe();

        System.out.println("zip... Done\n");

        // 是主线程不停
        System.in.read();

    }
}
