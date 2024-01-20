package top.sharehome.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Spring Security示例代码控制器
 *
 * @author AntonyCheng
 */
@RestController
public class SecurityController {

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/insert")
    public Mono<String> insert(){
        return Mono.just("增加成功");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/delete")
    public Mono<String> delete(){
        return Mono.just("删除成功");
    }

    @PreAuthorize("hasRole('other')")
    @GetMapping("/select")
    public Mono<String> select(){
        return Mono.just("查询成功");
    }

    @PreAuthorize("hasRole('other')")
    @GetMapping("/update")
    public Mono<String> update(){
        return Mono.just("修改成功");
    }

}
