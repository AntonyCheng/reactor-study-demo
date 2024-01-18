package top.sharehome.demo02springwebflux.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 抓取数学计算异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Mono<String> exceptionHandler(RuntimeException e) {
        return Mono.just("Global exception handler... " + e.getClass());
    }

}
