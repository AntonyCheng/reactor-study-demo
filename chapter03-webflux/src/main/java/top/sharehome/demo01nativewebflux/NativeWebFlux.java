package top.sharehome.demo01nativewebflux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 使用原生的HttpServer和HttpHandler创建一个服务器
 * 该服务器只做一件事：获取到用户的URL之后，响应URL+" Hello!"
 *
 * @author AntonyCheng
 */
public class NativeWebFlux {

    /**
     * 方法入口
     *
     * @param args 参数
     * @author AntonyCheng
     */
    public static void main(String[] args) throws IOException {

        // 1、使用HttpHandler创建一个请求响应执行器
        HttpHandler httpHandler = (request, response) -> {

            // 首先拿到URL
            URI uri = request.getURI();
            // 打印收到的数据
            System.out.println("Requests received by the server: " + uri);
            // 组装响应结果
            String responseText = uri + " Hello!";
            // 创建响应缓冲区工厂
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 将响应结果放入工厂加工成数据缓冲对象
            DataBuffer dataBuffer = dataBufferFactory.wrap(responseText.getBytes(StandardCharsets.UTF_8));
            // 将数据缓冲对象写入响应，写入后即表示响应完成，可以直接返回
            return response.writeWith(Mono.just(dataBuffer));

        };

        // 2、将执行器封装在ReactorHttpHandlerAdaptor适配器中
        ReactorHttpHandlerAdapter reactorHttpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);

        // 3、执行器编写好之后需要使用HttpServer开启一个监听localhost:8080的服务器
        HttpServer
                // 创建一个服务器
                .create()
                // 指定主机
                .host("localhost")
                // 指定端口
                .port(8080)
                // 指定需要绑定的适配器
                .handle(reactorHttpHandlerAdapter)
                // 绑定代表开启服务器，监听开启事件
                .doOnBind(httpServerConfig -> {
                    System.out.println("The server starting...");
                })
                // 立即开启服务器
                .bindNow();


        // 4、保证服务器一直运行
        System.in.read();
        // 5、给出断开连接的提示
        System.out.println("The server is disconnected...");

    }

}
