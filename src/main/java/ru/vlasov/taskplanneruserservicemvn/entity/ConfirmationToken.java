package ru.vlasov.taskplanneruserservicemvn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "confirm_tokens")
@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private UUID token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Column(name = "expired")
    private Date expired; // not used yet in current functional
}
