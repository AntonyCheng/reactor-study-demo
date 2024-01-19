package top.sharehome.demo02springdata.top.sharehome.springdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * 启动类
 * 在通过SpringBootData使用R2DBC时有几个比较重要的自动配置
 * 1、R2dbcAutoConfiguration：主要配置连接工厂、连接池；
 * 2、R2dbcDataAutoConfiguration：
 * （1）加载R2dbcEntityTemplate，提供基础的CRUD接口；
 * （2）加载数据类型映射器、转换器以及自定义的转换器组件：R2dbcCustomConversions；
 * 3、R2dbcRepositoriesAutoConfiguration：开启SpringData声明式（注解式）CRUD接口；
 * 4、R2dbcTransactionManagerAutoConfiguration：提供事务管理配置。
 *
 * @author AntonyCheng
 */
@SpringBootApplication
// 开启R2DBC的Repository仓库，以便直接使用SpringBoot Data中的父接口方法
@EnableR2dbcRepositories
public class R2dbcMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(R2dbcMainApplication.class, args);
    }

}
