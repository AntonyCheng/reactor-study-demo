package top.sharehome.security.model.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "t_perm")
public class Perm {

    @Id
    private Long id;

    private String value;

    private String uri;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
