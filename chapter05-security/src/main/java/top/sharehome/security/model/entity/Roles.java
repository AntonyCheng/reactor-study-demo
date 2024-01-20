package top.sharehome.security.model.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "t_roles")
public class Roles {
    @Id
    private Long id;

    private String name;

    private String value;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
