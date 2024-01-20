package top.sharehome.security.model.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "t_role_perm")
public class RolePerm {
    @Id
    private Long id;

    private Long roleId;

    private Long permId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
