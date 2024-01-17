package top.sharehome.demo02springwebflux.controller;

import com.alibaba.fastjson2.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Hello控制器
 *
 * @author AntonyCheng
 */
@RestController
public class HelloController {

    /**
     * 过去Servlet的方法或者注解在这里也能够使用，WebFlux兼容大部分Servlet的方法
     * 访问：localhost:8080/helloServlet?name=XXX
     */
    @GetMapping("/helloServlet")
    public String helloServlet(@RequestParam(name = "name", required = false, defaultValue = "default") String name) {
        return "Hello! " + name + ", I'm Servlet in WebFlux!";
    }

    /**
     * 现在WebFlux中有一套属于自己的写法，但是不推荐，因为这样写会和Servlet方式形成两种编码风格，不容易进行维护
     * 访问：localhost:8080/helloRouteFunction?name=XXX
     */
    @Bean
    public RouterFunction<?> hello1v1RouteFunction() {
        // 这是一个Bean中路由和接口一对一的情况
        return route(
                GET("/hello1v1RouteFunction"),
                request -> {
                    Optional<String> nameOptional = request.queryParam("name");
                    String name = "default";
                    if (nameOptional.isPresent()) {
                        name = nameOptional.get();
                    }
                    return ok().body(Mono.just("Hello! " + name + ", I'm 1v1 RouterFunction in WebFlux!"), String.class);
                });
    }

    @Bean
    public RouterFunction<?> hello1vNRouteFunction() {
        // 这是一个Bean中路由和接口一对多的情况
        return route()
                .GET("/hello1vNRouteFunction1", request -> {
                    Optional<String> nameOptional = request.queryParam("name");
                    String name = "default";
                    if (nameOptional.isPresent()) {
                        name = nameOptional.get();
                    }
                    return ok().body(Mono.just("Hello! " + name + ", I'm 1vN RouterFunctionGet in WebFlux!"), String.class);
                })
                .POST("/hello1vNRouteFunction2", request -> {
                    return ok().body(request.bodyToMono(String.class)
                            .map(json -> JSON.parseObject(json, HashMap.class).get("name"))
                            .defaultIfEmpty("default")
                            .onErrorReturn("default")
                            .map(n -> "Hello! " + n + ", I'm 1vN RouterFunctionPost in WebFlux!"), String.class);
                }).build();
    }

    public static void main(String[] args) {
        JSON.parseObject("{}", HashMap.class).get("name");
    }

    /**
     * 最推荐的写法就是按照Servlet习惯使用WebFlux响应
     * 其实很简单，就是将需要相应的数据使用Mono或者Flux封装上返回即可
     * 访问：localhost:8080/helloMono?name=XXX & localhost:8080/helloFlux?name=XXX
     */
    @GetMapping("/helloMono")
    public Mono<String> helloMono(@RequestParam(name = "name", required = false, defaultValue = "default") String name) {
        return Mono.just("Hello! " + name + ", I'm Mono in WebFlux!");
    }

    @GetMapping("/helloFlux")
    public Flux<String> helloFlux(@RequestParam(name = "name", required = false, defaultValue = "default") String name) {
        return Flux.just("Hello! " + name + ", I'm Flux in WebFlux!", "Hello! " + name + ", I'm Flux in WebFlux AGAIN!");
    }

    /**
     * 这种方式在这里总结一下：
     * 1、返回单个数据使用Mono响应，返回多个数据使用Flux响应；
     * 2、使用Flux还可以实现服务端事件推送（SSE，即Server Send Event），可能单说非常陌生，但是想一想ChatGPT就能够有一些感悟，接下来实现一下；
     * SSE的实现需要明确指定产生的响应介质类型："text/event-stream"，不然浏览器不会持续接收数据，在static静态资源中有一个index.html文件，直接访问localhost:8080即可查看效果。
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> helloSse(@RequestParam(name = "name", required = false, defaultValue = "default") String name) {
        return Flux.just("Hello! " + name + ", I'm Server Send Event in WebFlux!")
                // 把内容打散
                .flatMap(str -> Flux.fromArray(str.split(" ")))
                // 每个内容均延迟发送
                .delayElements(Duration.ofMillis(1000));
    }

    /**
     * 其实对于SSE机制来说它有一种自己的编码方式，即响应Flux<ServerSentEvent<T>>类，
     * 麻烦一点，但是能够更好的控制传输的数据详细内容，这样发送能够做到更加接近于ChatGPT的接口响应。
     */
    @GetMapping(value = "/sse/self", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> helloSseSelf(@RequestParam(name = "name", required = false, defaultValue = "default") String name) {
        return Flux.just("Hello! " + name + ", I'm Server Send Event in WebFlux!")
                // 打散为单词
                .flatMap(str -> Flux.fromArray(str.split(" "))
                        // 把每个单词转换成ServerSentEvent类
                        .map(s -> ServerSentEvent.builder(s)
                                .id(UUID.randomUUID().toString())
                                .comment("some about this data")
                                .event("the event that send data")
                                .build()))
                // 将每个ServerSentEvent对象延迟发送
                .delayElements(Duration.ofMillis(1000));
    }


}
