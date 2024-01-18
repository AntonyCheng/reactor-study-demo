package top.sharehome.demo02springwebflux.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 全局异常处理演示控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/exception")
public class Demo02ExceptionController {

    /**
     * 演示普通接口发生异常通过全局异常处理器抓取后的效果
     */
    @GetMapping("/normal/{num}")
    public Mono<Integer> normalException(@PathVariable("num") Integer num) {
        for (int i = 0; i < 10; i++) {
            int temp = 10 / (9 - i);
        }
        return Mono.just(num);
    }

    /**
     * 演示SSE发生异常通过全局异常处理器抓取后的效果
     * 推给前端的事件会直接被中止
     */
    @GetMapping(value = "/sse/{num}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sseException(@PathVariable("num") Integer num) {
        return Flux.just(1, 2, 3, 0, 7, 8, 9, num)
                .map(integer -> {
                    String s = "" + (integer/integer);
                    return s;
                })
                .delayElements(Duration.ofSeconds(1))
                .onErrorContinue((error,data) -> {
                    System.out.println(data);
                    throw new RuntimeException(error);
                });
    }

}
