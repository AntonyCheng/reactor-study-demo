package top.sharehome.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

/**
 * Security启动类
 * Spring Security自动配置行为如下：
 * 1、SecurityAutoConfiguration（SpringMVC中的配置）：导入SecurityFilterChain组件 ==> 默认所有请求都需要登录才可以访问，以及加载默认登录页。
 * 2、ReactiveSecurityAutoConfiguration
 * 3、
 * 4、
 *
 * @author AntonyCheng
 */
@SpringBootApplication
@EnableR2dbcRepositories
// 开启方法级别的权限注解
@EnableReactiveMethodSecurity
public class SecurityMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityMainApplication.class, args);
    }

}
