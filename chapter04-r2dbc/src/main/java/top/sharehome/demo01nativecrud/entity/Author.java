package top.sharehome.demo01nativecrud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实体类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Author implements Serializable {

    @Serial
    private static final long serialVersionUID = -7646905657624355586L;

    private Long id;

    private String name;

}
