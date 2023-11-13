package ru.vlasov.taskplanneruserservicemvn.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlasov.taskplanneruserservicemvn.dto.UserProfileDto;
import ru.vlasov.taskplanneruserservicemvn.exception.UserNotAuthenticated;
import ru.vlasov.taskplanneruserservicemvn.service.AppUserService;
import ru.vlasov.taskplanneruserservicemvn.service.ConverterDtoService;

import java.security.Principal;

@RestController
@RequestMapping(value =  "/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;
    private final ConverterDtoService converterDtoService;

    @GetMapping
    public ResponseEntity<?> getUser(Principal principal) {
        UserProfileDto userProfileDto = null;
        if (principal != null) {
            userProfileDto = converterDtoService.doFromUserToUserProfile(appUserService.findByEmail(principal.getName()));
        } else {
            throw new UserNotAuthenticated();
        }

        return ResponseEntity.ok(userProfileDto);
    }
}
