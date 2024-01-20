package top.sharehome.security.model.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "t_user")
public class User {
    @Id
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
