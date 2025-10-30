package com.project.supplychain.models.user;

import com.project.supplychain.enums.Roles;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.SalesOrderLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User{
    private String name;
    private String telephone;
    private String address;
    @Enumerated(EnumType.STRING)
    private Roles role = Roles.CLIENT ;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrder> salesOrders = new ArrayList<>();
}
