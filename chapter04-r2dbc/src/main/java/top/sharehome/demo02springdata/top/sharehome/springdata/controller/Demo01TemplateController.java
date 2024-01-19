package top.sharehome.demo02springdata.top.sharehome.springdata.controller;

import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;

import java.time.Duration;
import java.util.List;

/**
 * R2DBC Template演示控制器
 * 这里主要演示使用R2dbcEntityTemplate API以及DatabaseClient数据库客户端进行增删查改操作
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/template")
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
    public Mono<Author> selectById(@PathVariable("id") Long id) {
        // 1、使用Criteria类构造查询条件
        Criteria criteria = Criteria
                .empty()
                .and("id").is(id);
        // 2、封装为Query对象
        Query query = Query
                .query(criteria);
        // 3、使用使用Template进行查询
        return r2dbcEntityTemplate
                .selectOne(query, Author.class);
    }

    /**
     * 增加数据
     */
    @PostMapping("/insert")
    public Mono<String> insert(@RequestBody Author author) {
        return Flux.range(0, 3)
                .flatMap(i -> {
                    author.setId(author.getId() + i);
                    author.setName(author.getName() + i);
                    return r2dbcEntityTemplate.insert(author);
                })
                .then(Mono.just("增加成功"));
    }

    /**
     * 根据ID删除数据
     */
    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteById(@PathVariable Long id) {
        return r2dbcEntityTemplate.delete(
                        Query.query(Criteria
                                .empty()
                                .and("id").is(id)
                        ), Author.class)
                .then(Mono.just("删除成功"));
    }

    /**
     * 根据ID修改数据
     */
    @PutMapping("/update")
    public Mono<String> updateById(@RequestBody Author author) {
        return r2dbcEntityTemplate.update(
                Query.query(Criteria.empty().and("id").is(author.getId())),
                Update.update("name", author.getName()),
                Author.class
        ).then(Mono.just("修改成功"));
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public Mono<List<Author>> list() {
        return r2dbcEntityTemplate.select(Query.empty(), Author.class)
                .collectList();
    }

    /**
     * 获取列表SSE
     */
    @GetMapping(value = "/list/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Author> listSse() {
        return r2dbcEntityTemplate.select(Query.empty(), Author.class)
                .map(author -> author).delayElements(Duration.ofMillis(1000));
    }

    /**
     * 联表查询
     */
    @GetMapping("/join")
    public Mono<List<String>> join() {
        return databaseClient.sql("select t_author.id as aid,t_author.name as aname,tb.author_id as bid,tb.title as btitle,tb.publish_time as btime from t_author right join `t_book` as tb on t_author.id = tb.author_id")
                .fetch()
                .all()
                .map(map -> {
                    Object aid = map.get("aid");
                    Object aname = map.get("aname");
                    Object bid = map.get("bid");
                    Object btitle = map.get("btitle");
                    Object btime = map.get("btime");
                    return aid + "\t" + aname + "\t" + bid + "\t" + btitle + "\t" + btime;
                })
                .collectList();
    }

}
