package top.sharehome.demo02springwebflux.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

/**
 * WebFlux参数演示控制器
 * 主要对比一些和SpringMVC中不一样的控制器参数：请求、响应以及Session；
 * 其他的参数两者均兼容。
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/params")
public class Demo03ParamsController {

    @GetMapping("/mvc/request/response")
    public String getRequestResponseByMvc(
            // 获取请求响应
            HttpServletRequest request, HttpServletResponse response,
            // 获取Session
            HttpSession session
    ) {
        System.out.println("request = " + request);
        System.out.println("response = " + response);
        System.out.println("session = " + session);
        return "OK";
    }

    @GetMapping("/flux/request/response")
    public Mono<String> getRequestResponseByFlux(
            // 获取请求响应
            ServerWebExchange exchange, HttpServerRequest request, HttpServletResponse response,
            // 获取Session
            WebSession session
    ) {
        System.out.println("request in exchange = " + exchange.getRequest());
        System.out.println("response in exchange = " + exchange.getResponse());
        System.out.println("request = " + request);
        System.out.println("response = " + response);
        System.out.println("session = " + session);
        return Mono.just("OK");
    }

}
