package com.project.supplychain.models.user;

import com.project.supplychain.enums.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
public class Admin extends User{
    @Enumerated(EnumType.STRING)
    protected Roles role ;

     public Admin() {
        this.role = Roles.ADMIN;
    }
}
