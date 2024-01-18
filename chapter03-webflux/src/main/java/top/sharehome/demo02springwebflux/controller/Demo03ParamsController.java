package top.sharehome.demo02springwebflux.controller;


import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

/**
 * WebFlux参数演示控制器
 * 主要对比一些和SpringMVC中不一样的控制器参数：请求响应、Session以及文件参数；
 * 在SpringMVC中请求响应、Session以及文件参数分别对应的是：（HttpServletRequest&HttpServletResponse）、HttpSession以及MultipartFile。
 * 在WebFlux中请求响应、Session以及文件参数分别对应的是：   ServerWebExchange、WebSession以及FilePart。
 * 其他的参数两者均兼容。
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/params")
public class Demo03ParamsController {

    @PostMapping("/default")
    public String getDefaultParams(
            // 获取请求响应
            ServerWebExchange exchange,
            // 获取Session
            WebSession session,
            // 获取文件参数
            FilePart filePart
    ) {
        System.out.println("exchange.getRequest() = " + exchange.getRequest());
        System.out.println("exchange.getResponse() = " + exchange.getResponse());
        System.out.println("session = " + session);
        System.out.println("filePart.filename() = " + filePart.filename());
        return "OK";
    }

}
