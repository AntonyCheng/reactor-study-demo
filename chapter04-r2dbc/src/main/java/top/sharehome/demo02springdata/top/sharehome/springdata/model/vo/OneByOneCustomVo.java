package top.sharehome.demo02springdata.top.sharehome.springdata.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Author;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 1-1联表复杂查询结果实体类（包含自定义的数据类型）
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OneByOneCustomVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2898713863947416897L;
    @Id
    private Long bid;

    private String btitle;

    private LocalDateTime btime;

    private Long aid;

    private Author author;

}
