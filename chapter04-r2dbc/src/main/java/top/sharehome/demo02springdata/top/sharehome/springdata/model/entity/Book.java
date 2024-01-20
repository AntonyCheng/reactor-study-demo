package top.sharehome.demo02springdata.top.sharehome.springdata.model.entity;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 书籍实体类
 *
 * @author AntonyCheng
 */
@Table("t_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = -6542893977053156200L;

    @Id
    private Long id;

    private String title;

    private Long authorId;

    private ZonedDateTime publishTime;

}
