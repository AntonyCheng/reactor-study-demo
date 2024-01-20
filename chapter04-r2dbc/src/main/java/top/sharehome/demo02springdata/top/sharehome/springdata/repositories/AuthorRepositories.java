package top.sharehome.demo02springdata.top.sharehome.springdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByOneNativeVo;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;

import java.util.Collection;

/**
 * 作者仓库实体类
 * 继承R2dbcRepository接口即可
 *
 * @author AntonyCheng
 */
@Repository
public interface AuthorRepositories extends R2dbcRepository<Author, Long> {

    // 此时默认继承了一堆CRUD方法，就像Mybatis-Plus一样

    // 自定义方法（仅限单表复杂条件查询，例如where id = ? in () and name like ?）
    Flux<Author> findAllByIdInAndNameLike(Collection<Long> id, String name);

    // 自定义SQL（可用于联表复杂条件查询）
    @Query("select t_author.id as aid,t_author.name as aname,tb.author_id as bid,tb.title as btitle,tb.publish_time as btime from t_author right join `t_book` as tb on t_author.id = tb.author_id")
    Flux<OneByOneNativeVo> findComplex();
}
