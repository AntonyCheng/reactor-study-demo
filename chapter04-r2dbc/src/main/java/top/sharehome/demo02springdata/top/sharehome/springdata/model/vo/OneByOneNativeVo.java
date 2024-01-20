package top.sharehome.demo02springdata.top.sharehome.springdata.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 1-1联表复杂查询结果实体类（仅包含Java原生包装类型）
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OneByOneNativeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8876416559390587957L;

    private Long aid;

    private String aname;

    private Long bid;

    private String btitle;

    private LocalDateTime btime;

}
