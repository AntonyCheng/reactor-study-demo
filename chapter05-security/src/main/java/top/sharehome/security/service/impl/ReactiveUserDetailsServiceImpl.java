package top.sharehome.security.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 默认的用户认证接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    // 自定义根据用户名查询用户基本信息
    @Override
    public Mono<UserDetails> findByUsername(String username) {

        // 这里就不计划如何对数据进行查询了，直接进行结果的构建
        UserDetails userDetails = User.builder()
                .username(username)
                .password("123456")
                .roles("admin")
                .build();

        return Mono.just(userDetails);
    }
}
