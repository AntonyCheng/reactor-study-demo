package top.sharehome.demo03error;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

/**
 * 错误处理示例代码
 * 错误是在大部分情况下是一种中断，在以往的学习中也接触过以下错误处理：
 * 1、Flux.doOnError  ==>  探测数据流处理过程中发生错误的情况，但是此处依旧会向订阅者发布改错误情况；
 * 2、Flux.just().subscribe(hookOnError)  ==>  探测数据流订阅过程中发生的错误情况。
 * 而这里的错误处理示例展示的是数据流处理过程发生错误的情况，即Flux.onErrorXXX()的一些列方法。
 *
 * @author AntonyCheng
 */

public class ErrorDemo {

    /**
     * 1、以往学习过程中接触到的简单数据流错误处理的示例代码
     */
    private static void easyError() {
        Flux.just(1, 2, 0, 4)
                .map(data -> {
                    System.out.println(100 / data);
                    return 100 / data;
                })
                .doOnError(System.out::println)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println(throwable);
                    }
                });
    }

    /**
     * 2、让数据流直接抛出错误
     */
    private static void directError() {
        Flux.error(new RuntimeException())
                .subscribe(new BaseSubscriber() {
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println(throwable);
                    }
                });
    }

    /**
     * 3、捕获错误返回一个静态默认值
     * 特点：吃掉错误，订阅者无错误感知，返回一个静态默认值，并且让流正常完成，但是数据流传输会终止于错误元素。
     */
    private static void catchAndReturnStaticDefault() {
        // 1、使用onErrorReturn()方法返回一个默认值
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorReturn(3)
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });

        // 2、使用onErrorResume方法返回一个Mono
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorResume(throwable -> Mono.just(3))
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 4、捕获错误并且执行兜底方法
     * 特点：吃掉错误，订阅者无错误感知，返回一个数据流，并且让数据流正常完成，但是数据流传输会终止于错误元素。
     */
    private static void catchAndFallbackMethod() {
        // 使用onErrorResume方法
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorResume(throwable -> fallbackMethod())
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 兜底函数
     */
    private static Mono<Integer> fallbackMethod() {
        return Mono.just(-1);
    }

    /**
     * 5、捕获错误并且将错误转换为自定义错误
     * 特点：吃掉原错误，重抛订阅者可感知的新错误，数据流异常完成，传输会终止于错误元素。
     */
    private static void catchAndWrapThenReThrow() {
        // 1、使用onErrorResume方法返回一个新错误
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorResume(throwable -> Flux.error(new CustomException("custom exception...")))
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });

        // 2、使用onErrorMap方法返回一个新错误
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorMap(throwable -> new CustomException("custom exception..."))
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 自定义错误
     */
    private static class CustomException extends RuntimeException {
        CustomException(String message) {
            super(message);
        }
    }

    /**
     * 6、捕获错误并且记录日志
     * 特点：当发生错误时记录日志，数据流异常完成，传输会终止于错误元素。
     */
    private static void catchAndLogThenThrow() {
        // 使用doOnError方法记录日志
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .doOnError(throwable -> {
                    System.out.println("log error ...");
                })
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 7、捕获错误，处理错误，并且设置数据流处理的终止方法
     * 特点：当发生错误时处理错误，在数据流结束工作后执行最终方法，数据流异常完成，传输会终止于错误元素。
     */
    private static void catchAndHandleThenFinally() {
        // 将doOnError、doFinally方法组合使用
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .doOnError(throwable -> {
                    System.out.println("log error ...");
                })
                .doFinally(signalType -> {
                    System.out.println("finally: " + signalType);
                })
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 8、忽略掉错误，仅作日志记录，不终止数据流的传输
     * 特点：抓取错误并记录日志，然后进行下一轮的订阅，让数据流正常完成。
     */
    private static void catchAndLogThenIgnore() {
        // 使用onErrorContinue方法记录日志
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorContinue((throwable, data) -> {
                    System.out.println("log error: " + throwable + " data: " + data);
                })
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 9、抓取错误之后，不做任何处理，立刻正常完成整个数据流的传输
     */
    private static void catchAndHandleThenComplete() {
        // 使用onErrorComplete方法记录日志
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorComplete()
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 9、抓取错误之后，不做任何处理，立刻异常完成整个数据流的传输
     */
    private static void catchAndHandleThenStop() {
        // 使用onErrorStop方法记录日志
        Flux.just(1, 2, 0, 4)
                .map(data -> 100 / data)
                .onErrorStop()
                .subscribe(data -> {
                    System.out.println("onNext: " + data);
                }, throwable -> {
                    System.out.println("onError: " + throwable);
                });
    }

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException {
//        easyError();
//        directError();
//        catchAndReturnStaticDefault();
//        catchAndFallbackMethod();
//        catchAndWrapThenReThrow();
//        catchAndLogThenThrow();
//        catchAndHandleThenFinally();
//        catchAndLogThenIgnore();
        catchAndHandleThenComplete();

        System.in.read();
    }

}
