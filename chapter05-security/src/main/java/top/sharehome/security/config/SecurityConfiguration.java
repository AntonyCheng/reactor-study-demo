package top.sharehome.security.config;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security配置类
 * 需求：静态资源放行，其他资源需要认证
 *
 * @author AntonyCheng
 */
@Configuration
public class SecurityConfiguration {

    @Resource
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        // 1、定义哪些请求需要认证，哪些不需要
        http.authorizeExchange(authorize -> {
            // 允许所有人访问静态资源
            authorize.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            // 剩下的所有请求都需要认证
            authorize.anyExchange().authenticated();
        });

        // 2、开启默认的表单登录
        http.formLogin(form -> {

        });

        // 3、禁用安全控制
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        // 4、配置认证规则
        // Spring Security底层使用ReactiveAuthenticationManager去查询用户信息
        // ReactiveAuthenticationManager有一个实现：
        //      UserDetailsRepositoryReactiveAuthenticationManager：用户信息去数据库查询，
        //      而UserDetailsRepositoryReactiveAuthenticationManager需要ReactiveUserDetailsService，
        //      所以我们只需要实现一个ReactiveUserDetailsService即可；
        http.authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService));

        return http.build();
    }

}
