package ru.vlasov.taskplanneruserservicemvn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Role extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<AppUser> users;

    public Role(String name) {
        this.name = name;
        this.status = Status.ACTIVE;
    }
}
