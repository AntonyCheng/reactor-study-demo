package top.sharehome.demo02springwebflux.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux过滤器示例代码
 * 首先明确WebFlux中没有Interceptor拦截器的相关实现，框架已经将其与Filter过滤器进行了合并，构成了如今的WebFilter接口
 * 在WebFlux中Filter和SpringMVC中的过滤器有一定区别
 *
 * @author AntonyCheng
 */
@Component
public class WebFluxFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 获取响应
        ServerHttpResponse response = exchange.getResponse();
        // 开始过滤，注意在SpringMVC中过滤器是先处理来时的请求，然后把请求投喂给目标方法，目标方法响应之后将结果返给过滤器，过滤器再做最后的响应，
        // 但是在WebFlux中是全异步的，所以想在目标方法将响应结果返回给过滤器之后在过滤器中继续执行操作必须是在职责链条进行。
        Mono<Void> filter = chain
                .filter(exchange)
                .doOnError(System.out::println)
                .doFinally(signalType -> {
                    System.out.println("职责链之后的操作应该写在职责链中");
                });
        // 注意Mono属于全异步操作，所以在职责链filter之后的操作并不能在职责链下方编写
        return filter;
    }
}
