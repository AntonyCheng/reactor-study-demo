package top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.converter;

import io.r2dbc.spi.Row;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.vo.OneByOneCustomVo;

import java.time.LocalDateTime;

/**
 * 1对1联表复杂查询转换器
 *
 * @author AntonyCheng
 */
@Configuration
@ReadingConverter  // 读取数据库数据时使用
public class OneByOneCustomConverter implements Converter<Row, OneByOneCustomVo> {

    @Override
    public OneByOneCustomVo convert(Row row) {
        String aname = row.get("aname", String.class);
        Long bid = row.get("bid", Long.class);
        String btitle = row.get("btitle", String.class);
        Long aid = row.get("aid", Long.class);
        LocalDateTime btime = row.get("btime", LocalDateTime.class);

        Author author = new Author(aid, aname);

        return new OneByOneCustomVo(bid, btitle, btime, aid, author);
    }

    /**
     * 让转换去加入SpringBoot体系中
     */
    @Bean
    public R2dbcCustomConversions conversions() {
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new OneByOneCustomConverter());
    }
}
