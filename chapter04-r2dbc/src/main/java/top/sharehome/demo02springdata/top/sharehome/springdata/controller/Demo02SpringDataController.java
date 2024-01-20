package top.sharehome.demo02springdata.top.sharehome.springdata.controller;

import jakarta.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Book;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByManyCustomVo;
import top.sharehome.demo02springdata.top.sharehome.springdata.repositories.AuthorOneByOneCustomVoRepositories;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByOneCustomVo;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByOneNativeVo;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;
import top.sharehome.demo02springdata.top.sharehome.springdata.repositories.AuthorRepositories;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Resource
    private AuthorOneByOneCustomVoRepositories authorOneByOneCustomVoRepositories;

    @Resource
    private DatabaseClient databaseClient;

    /**
     * 根据ID查询作者数据
     */
    @GetMapping("/select/{id}")
    public Mono<Author> selectById(@PathVariable("id") Long id) {
        return authorRepositories.findById(id);
    }

    /**
     * 增加作者数据
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
     * 根据ID删除作者数据
     */
    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteById(@PathVariable Long id) {
        return authorRepositories.deleteById(id).then(Mono.just("删除成功"));
    }

    /**
     * 根据ID修改作者数据
     */
    @PutMapping("/update")
    public Mono<String> updateById(@RequestBody Author author) {
        return authorRepositories.save(author).then(Mono.just("修改成功"));
    }

    /**
     * 获取作者列表
     */
    @GetMapping("/list")
    public Mono<List<Author>> list() {
        return authorRepositories.findAll().collectList();
    }

    /**
     * 获取作者列表SSE
     */
    @GetMapping(value = "/list/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Author> listSse() {
        return authorRepositories.findAll().map(author -> author).delayElements(Duration.ofMillis(1000));
    }

    /**
     * 作者分页查询
     */
    @GetMapping("/page")
    public Mono<Page<Author>> page() {
        return authorRepositories.findBy(
                Example.of(new Author()),
                reactiveFluentQuery -> reactiveFluentQuery.page(Pageable.ofSize(10)));
    }

    /**
     * 按照一定复杂SQL查询作者列表
     * 单表复杂查询（使用自定义方法）
     */
    @GetMapping("/singleComplex")
    public Mono<List<Author>> singleComplex() {
        return authorRepositories.findAllByIdInAndNameLike(Arrays.asList(1L, 2L), "1").collectList();
    }

    /**
     * 关联图书表查作者数据，并且将查到的表字段作为封装类字段
     * 1对1联表复杂查询 ==> 封装类中所有字段均为Java原生的包装类型（使用自定义SQL，也可使用DatabaseClient客户端进行底层操作）
     */
    @GetMapping("/1v1NativeComplex")
    public Mono<List<OneByOneNativeVo>> oneByOneNativeComplex() {
        return authorRepositories.findComplex().collectList();
    }

    /**
     * 关联图书表查询作者数据，并且将查到的作者类作为单独字段
     * 1对1联表复杂查询 ==> 封装类中字段中包含自定义的类型（使用转换器+自定义SQL，更推荐使用DatabaseClient客户端进行底层操作）
     * 要使用转换器+自定义SQL，必须将查到的类型单独封装成一个Repositories，不然就会影响该实体类单表查询，
     * 就比如我如果直接在bookRepositories自定义SQL之后，针对Book设计转换器，那么之后每次有关Book的查询都会收到转换器的牵制。
     */
    @GetMapping("/1v1CustomComplex")
    public Mono<List<OneByOneCustomVo>> oneByOneCustomComplex() {
        return authorOneByOneCustomVoRepositories.findComplex().collectList();
    }

    /**
     * 关联图书表查询作者数据，并且将同一作者的书籍放在一个List中
     * 1对N联表复杂查询  ==>  直接使用DatabaseClient客户端进行底层操作
     */
    @GetMapping("/1vNCustomComplex")
    public Mono<List<OneByManyCustomVo>> oneByManyCustomComplex() {
        return databaseClient.sql("select a.name as aname,b.id as bid,b.title as btitle,b.author_id as aid,b.publish_time as btime from t_author as a right join t_book as b on b.author_id = a.id")
                .fetch()
                .all()
                // 面对一对多的查询需求时，选择bufferUtilChanged或者groupBy方法
                // 两者的区别就是前者需要拿到的数据已经根据分组字段进行了排序，后者则不没有这个要求
                .bufferUntilChanged(map -> (Long) map.get("aid"))
                .flatMap(list -> {
                    OneByManyCustomVo oneByManyCustomVo = new OneByManyCustomVo();
                    Map<String, Object> first = list.getFirst();
                    Long aid = (Long) first.get("aid");
                    String aname = (String) first.get("aname");
                    oneByManyCustomVo.setId(aid);
                    oneByManyCustomVo.setName(aname);
                    List<Book> books = new ArrayList<Book>();
                    return Flux.fromIterable(list)
                            .handle((map, sink) -> {
                                Long bid = (Long) map.get("bid");
                                String btitle = (String) map.get("btitle");
                                ZonedDateTime btime = (ZonedDateTime) map.get("btime");
                                books.add(new Book(bid, btitle, aid, btime));
                            })
                            .then(Mono.just(oneByManyCustomVo.setBookList(books)));
                })
                .collectList();
//                .groupBy(map -> map.get("aid"))
//                .flatMap(groupFlux -> {
//                    OneByManyCustomVo oneByManyCustomVo = new OneByManyCustomVo();
//                    Map<String, Object> first = list.getFirst();
//                    Long aid = (Long) first.get("aid");
//                    String aname = (String) first.get("aname");
//                    oneByManyCustomVo.setId(aid);
//                    oneByManyCustomVo.setName(aname);
//                    List<Book> books = new ArrayList<Book>();
//                    return Flux.fromIterable(list)
//                            .handle((map, sink) -> {
//                                Long bid = (Long) map.get("bid");
//                                String btitle = (String) map.get("btitle");
//                                ZonedDateTime btime = (ZonedDateTime) map.get("btime");
//                                books.add(new Book(bid, btitle, aid, btime));
//                            })
//                            .then(Mono.just(oneByManyCustomVo.setBookList(books)));
//                })
//                .collectList();
    }
}
