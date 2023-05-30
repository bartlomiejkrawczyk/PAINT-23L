package org.example.authorization.entity;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Value
@Table("USERS")
public class UserEntity {
    @Id
    @Column("USER_ID")
    Long userId;

    @Column("LOGIN")
    String login;

    @Column("PASSWORD_HASH")
    String passwordHash;
}
