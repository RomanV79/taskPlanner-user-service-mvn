package ru.vlasov.taskplanneruserservicemvn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser extends BaseEntity {

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "confirmed")
    @Builder.Default
    private boolean confirmed = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "appUser")
    private List<Task> tasks;

    public AppUser(String name, String email, String password, boolean confirmed, List<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmed = confirmed;
        this.roles = roles;
        this.status = Status.ACTIVE;
    }
}
