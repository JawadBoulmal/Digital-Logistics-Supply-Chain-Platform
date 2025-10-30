package com.project.supplychain.models.user;

import com.project.supplychain.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
@SuperBuilder
public abstract class User {
    @Id
    @GeneratedValue
    protected UUID id;
    protected String email;
    protected String password;

    @Builder.Default
    protected boolean active = true;

}
