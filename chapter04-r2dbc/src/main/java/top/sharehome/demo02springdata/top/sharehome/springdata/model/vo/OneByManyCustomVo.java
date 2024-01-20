package top.sharehome.demo02springdata.top.sharehome.springdata.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import top.sharehome.demo02springdata.top.sharehome.springdata.model.entity.Book;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 1-N联表复杂查询结果实体类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OneByManyCustomVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4985322527303291944L;

    @Id
    private Long id;

    private String name;

    private List<Book> bookList;

}
