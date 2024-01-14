package top.sharehome.demo04timeoutretry;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

/**
 * 超时重试示例代码
 *
 * @author AntonyCheng
 */

public class TimeoutRetryDemo {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException {
        // 下面模拟一个场景来演示超时重试的情况
        // 模拟包含3个元素的数据流
        Flux.just("1", "2","3")
                // 模拟每一个元素都要参与调用一次远程服务，可是每次调用都需要花两秒钟
                .delayElements(Duration.ofSeconds(2))
                // 打印一下日志
                .log()
                // 这里程序要求在1秒钟内得到响应不然就超时异常
                .timeout(Duration.ofSeconds(1))
                // 这里为了避免网络波动原因造成的响应超时，重试3次，如果不设置超时时间，那么就会一直重试
                // 注意；在Reactor中的重试是从数据流源头重新请求元素开始的，而不是从下一个元素处理时开始的，所以这个模拟示例会一直在请求“1”这个元素时超时异常
                .retry(3)
                // 这里就会搭配错误处理编写逻辑代码
                .onErrorReturn("Timeout...")
                .subscribe(System.out::println);

        System.in.read();
    }

}
