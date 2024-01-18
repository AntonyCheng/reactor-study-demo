package top.sharehome.demo02springwebflux.comfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux配置
 * 在SpringMVC中Web配置实现的是WebMvcConfigurer接口
 * 在WebFlux中Web配置实现的是WebFluxConfigurer接口
 *
 * @author AntonyCheng
 */
@Configuration
public class WebConfiguration implements WebFluxConfigurer {

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebFluxConfigurer.super.addCorsMappings(registry);
    }

    /**
     * 格式化配置
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebFluxConfigurer.super.addFormatters(registry);
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebFluxConfigurer.super.addResourceHandlers(registry);
    }

}
