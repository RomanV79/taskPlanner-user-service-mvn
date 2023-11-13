package ru.vlasov.taskplanneruserservicemvn.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.repository.AppUserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;

    public List<AppUser> getAll() {
        List<AppUser> userList;
        userList = userRepository.findAll();
        log.info("Get all users successfully, size list -> {}", userList.size());

        return userList;
    }

    public AppUser findByEmail(String email) {
        AppUser appUser = userRepository.findByEmail(email).orElseThrow();
        log.info("User with email: {} - successfully found", appUser.getEmail());
        return appUser;
    }

    public AppUser save(AppUser appUser) {
        return userRepository.save(appUser);
    }
}
