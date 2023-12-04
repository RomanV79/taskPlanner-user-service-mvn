package ru.vlasov.taskplanneruserservicemvn.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlasov.taskplanneruserservicemvn.config.security.AppUserDetails;
import ru.vlasov.taskplanneruserservicemvn.dto.AuthResponseDto;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.ConfirmationToken;
import ru.vlasov.taskplanneruserservicemvn.exception.ConfirmationNotSuccessfullyException;
import ru.vlasov.taskplanneruserservicemvn.service.*;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/confirmation-email")
public class ConfirmationController {

    private final ConfirmationTokenService confirmationTokenService;
    private final AppUserService appUserService;
    private final ProducerRabbitService producerRabbitService;
    private final ConverterDtoService converterDtoService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> confirm(@RequestParam("token") String token, Principal principal) {
        log.info("confirm token taken -> {}", token);
        if (!confirmationTokenService.confirm(token)) {
            throw new ConfirmationNotSuccessfullyException();
        }

        AppUser appUser = appUserService.findByEmail(principal.getName());
        AuthResponseDto confirmed = AuthResponseDto.builder()
                .token(jwtService.generateToken(new AppUserDetails(appUser)))
                .build();

        return ResponseEntity.ok(confirmed);
    }

    @GetMapping("/send-again")
    public ResponseEntity<?> sendConfirm(Principal principal) {
        log.info("Try send confirm email one more time, for user -> {}", principal.getName());
        ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationTokenByAppUser(appUserService.findByEmail(principal.getName()));
        if (confirmationToken == null) {
            throw new ConfirmationNotSuccessfullyException();
        }
        producerRabbitService.send(converterDtoService.createConfirmMessageFromConfirmationToken(confirmationToken));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
