package top.sharehome.demo02springdata.top.sharehome.springdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByOneCustomVo;

/**
 * 作者1-1自定义Vo仓库实体类
 * 继承R2dbcRepository接口即可
 *
 * @author AntonyCheng
 */
@Repository
public interface AuthorOneByOneCustomVoRepositories extends R2dbcRepository<OneByOneCustomVo, Long> {

    // 此时默认继承了一堆CRUD方法，就像Mybatis-Plus一样

    @Query("select a.name as aname,b.id as bid,b.title as btitle,b.author_id as aid,b.publish_time as btime from t_author as a right join t_book as b on b.author_id = a.id")
    Flux<OneByOneCustomVo> findComplex();

}
