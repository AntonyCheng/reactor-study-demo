package top.sharehome.demo02springdata.top.sharehome.springdata.controller;

import jakarta.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.dto.ComplexDto;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.repositories.AuthorRepositories;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用SpringBoot Data之后的控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/data")
public class Demo02SpringDataController {

    @Resource
    private AuthorRepositories authorRepositories;

    /**
     * 根据ID查询数据
     */
    @GetMapping("/select/{id}")
    public Mono<Author> selectById(@PathVariable("id") Long id) {
        return authorRepositories.findById(id);
    }

    /**
     * 增加数据
     * 注意：JPA体系中增加数据的主键id一定要为空，不然默认是更新操作
     */
    @PostMapping("/insert")
    public Mono<String> insert(@RequestBody Author author) {
        return Flux.range(0, 3)
                .flatMap(i -> {
                    return authorRepositories.save(new Author(null, author.getName() + i));
                })
                .then(Mono.just("增加成功"));
    }

    /**
     * 根据ID删除数据
     */
    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteById(@PathVariable Long id) {
        return authorRepositories.deleteById(id).then(Mono.just("删除成功"));
    }

    /**
     * 根据ID修改数据
     */
    @PutMapping("/update")
    public Mono<String> updateById(@RequestBody Author author) {
        return authorRepositories.save(author).then(Mono.just("修改成功"));
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public Mono<List<Author>> list() {
        return authorRepositories.findAll().collectList();
    }

    /**
     * 获取列表SSE
     */
    @GetMapping(value = "/list/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Author> listSse() {
        return authorRepositories.findAll().map(author -> author).delayElements(Duration.ofMillis(1000));
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Mono<Page<Author>> page() {
        return authorRepositories.findBy(
                Example.of(new Author()),
                reactiveFluentQuery -> reactiveFluentQuery.page(Pageable.ofSize(10)));
    }

    /**
     * 单表复杂查询（使用自定义方法）
     */
    @GetMapping("/singleComplex")
    public Mono<List<Author>> singleComplex() {
        return authorRepositories.findAllByIdInAndNameLike(Arrays.asList(1L, 2L), "1").collectList();
    }

    /**
     * 联表复杂查询使用自定义SQL）
     */
    @GetMapping("/manyComplex")
    public Mono<List<ComplexDto>> manyComplex() {
        return authorRepositories.findComplex().collectList();
    }

}
