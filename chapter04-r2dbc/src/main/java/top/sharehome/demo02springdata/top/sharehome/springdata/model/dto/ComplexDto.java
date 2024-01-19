package top.sharehome.demo02springdata.top.sharehome.springdata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 复杂查询结果实体类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ComplexDto {
    private Long aid;
    private String aname;
    private Long bid;
    private String btitle;
    private LocalDateTime btime;
}
