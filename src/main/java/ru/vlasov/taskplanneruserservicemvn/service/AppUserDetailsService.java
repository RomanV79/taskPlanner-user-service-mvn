package ru.vlasov.taskplanneruserservicemvn.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.config.security.AppUserDetails;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;

@Service
@Slf4j
public class AppUserDetailsService implements UserDetailsService {
    private final AppUserService appUserService;

    public AppUserDetailsService(AppUserService userService) {
        this.appUserService = userService;
    }

    @Override
    @Transactional
    public AppUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserService.findByEmail(email);
        if (appUser == null) {
            throw  new UsernameNotFoundException("User with email: " + email + " - doesn't found");
        }
        log.info("User with email: {} - successfully loaded", email);

        return new AppUserDetails(appUser);
    }
}
