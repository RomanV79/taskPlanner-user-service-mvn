package ru.vlasov.taskplanneruserservicemvn.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlasov.taskplanneruserservicemvn.dto.AuthResponseDto;
import ru.vlasov.taskplanneruserservicemvn.dto.AuthUserDto;
import ru.vlasov.taskplanneruserservicemvn.dto.RegistrationUserDto;
import ru.vlasov.taskplanneruserservicemvn.service.AuthenticationService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createJwtToken(@RequestBody AuthUserDto authUserDto) {
        return ResponseEntity.ok(authService.authenticate(authUserDto));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUserDto registrationUserDto) {
        AuthResponseDto registered = authService.register(registrationUserDto);
        return ResponseEntity.ok(registered);
    }
}
