package top.sharehome.demo02springdata.top.sharehome.springdata.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * 作者实体类
 *
 * @author AntonyCheng
 */
@Table("t_author")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Author implements Serializable {

    @Serial
    private static final long serialVersionUID = -7646905657624355586L;

    @Id
    private Long id;

    private String name;

}
