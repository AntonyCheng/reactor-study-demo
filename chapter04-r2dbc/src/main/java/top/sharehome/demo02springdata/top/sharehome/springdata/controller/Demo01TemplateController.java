package top.sharehome.demo02springdata.top.sharehome.springdata.controller;

import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.sharehome.demo02springdata.top.sharehome.springdata.entity.Author;

/**
 * todo: 补充增删查改以及join查询的示例
 * R2DBC Template演示控制器
 * 这里主要演示使用R2dbcEntityTemplate API以及DatabaseClient数据库客户端进行增删查改操作
 *
 * @author AntonyCheng
 */
@RestController
public class Demo01TemplateController {

    /**
     * 适用于单表操作，即简单的CRUD
     */
    @Resource
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * 适用于多表操作，即复杂的操作（join...）
     */
    @Resource
    private DatabaseClient databaseClient;

    /**
     * 根据ID查询数据
     */
    @GetMapping("/select/{id}")
    public Flux<Author> selectById(@PathVariable("id") Long id) {
        // 1、使用Criteria类构造查询条件
        Criteria criteria = Criteria
                .empty()
                .and("id").is(id);
        // 2、封装为Query对象
        Query query = Query
                .query(criteria);
        // 3、使用使用Template进行查询
        return r2dbcEntityTemplate
                .select(query, Author.class);
    }

}
