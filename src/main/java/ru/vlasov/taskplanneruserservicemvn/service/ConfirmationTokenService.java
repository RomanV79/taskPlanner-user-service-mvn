package ru.vlasov.taskplanneruserservicemvn.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.ConfirmationToken;
import ru.vlasov.taskplanneruserservicemvn.repository.ConfirmationTokenRepository;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmationTokenService {

    @Value("${app.token-confirm.lifetime}")
    private Duration tokenConfirmationLifeTime;
    private final ConfirmationTokenRepository repository;
    private final AppUserService appUserService;

    public ConfirmationToken createToken(AppUser appUser) {

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + tokenConfirmationLifeTime.toMillis());

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(UUID.randomUUID())
                .appUser(appUser)
                .expired(expiredDate)
                .build();

        repository.save(confirmationToken);

        return confirmationToken;
    }

    public ConfirmationToken findToken(String token) {
        UUID uuid = UUID.fromString(token);
        return repository.findByToken(uuid).orElse(null);
    }

    public boolean confirm(String token) {
        UUID uuidToken;
        try {
            uuidToken = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            return false;
        }

        ConfirmationToken confirmationToken = repository.findByToken(uuidToken).orElse(null);
        if (confirmationToken == null) {
            return false;
        }
        ;
        AppUser appUser = appUserService.findByEmail(confirmationToken.getAppUser().getEmail());
        appUser.setConfirmed(true);
        appUserService.save(appUser);
        repository.delete(confirmationToken);

        return true;
    }

    public ConfirmationToken findConfirmationTokenByAppUser(AppUser appUser) {
        return repository.findByAppUser(appUser).orElse(null);
    }

}
