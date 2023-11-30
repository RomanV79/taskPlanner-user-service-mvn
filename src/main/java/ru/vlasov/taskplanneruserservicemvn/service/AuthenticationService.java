package ru.vlasov.taskplanneruserservicemvn.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.config.security.AppUserDetails;
import ru.vlasov.taskplanneruserservicemvn.dto.AuthResponseDto;
import ru.vlasov.taskplanneruserservicemvn.dto.AuthUserDto;
import ru.vlasov.taskplanneruserservicemvn.dto.RegistrationUserDto;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.Status;
import ru.vlasov.taskplanneruserservicemvn.exception.PasswordsNotSameException;
import ru.vlasov.taskplanneruserservicemvn.exception.UserAlreadyExistException;
import ru.vlasov.taskplanneruserservicemvn.repository.AppUserRepository;
import ru.vlasov.taskplanneruserservicemvn.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final String DEFAULT_ROLE = "ROLE_USER";
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final ProducerRabbitService producerRabbitService;
    private final ConverterDtoService converterDtoService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponseDto authenticate(AuthUserDto authUserDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authUserDto.getUsername(), authUserDto.getPassword())
            );
            log.info("User with email: {} - successfully authenticated", authUserDto.getUsername());
        } catch (BadCredentialsException e) {
            log.info("User with email: {} - not authenticated", authUserDto.getUsername());
            throw new BadCredentialsException("");
        }

        var appUser = Optional
                .ofNullable(appUserRepository.findByEmail(authUserDto.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found exception"));

        return AuthResponseDto.builder()
                .token(jwtService.generateToken(new AppUserDetails(appUser.get())))
                .build();
    }

    public AuthResponseDto register(RegistrationUserDto registrationUserDto) {

        if (isPasswordsNotEqual(registrationUserDto)) {
            throw new PasswordsNotSameException();
        }

        var appUser = createAppUser(registrationUserDto);
        try {
            appUserRepository.save(appUser);
            log.info("User successfully saved in database -> {}", registrationUserDto.getUsername());
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                    && e.getMessage().contains("duplicate key value violates unique constraint")) {
                throw new UserAlreadyExistException();
            }
        }

        var confirmationToken = confirmationTokenService.createToken(appUser);
        log.info("Token for user: {} - is produced", registrationUserDto.getUsername());
        producerRabbitService.send(converterDtoService.createConfirmMessageFromConfirmationToken(confirmationToken));
        log.info("Token for user: {} - sent to rabbitMQ", registrationUserDto.getUsername());

        return AuthResponseDto.builder()
                .token(jwtService.generateToken(new AppUserDetails(appUser)))
                .build();
    }

    private AppUser createAppUser(RegistrationUserDto registrationUserDto) {
        return AppUser.builder()
                .name(registrationUserDto.getName())
                .email(registrationUserDto.getUsername())
                .password(passwordEncoder.encode(registrationUserDto.getPassword()))
                .status(Status.ACTIVE)
                .roles(List.of(roleRepository.findByName(DEFAULT_ROLE)))
                .build();
    }

    private static boolean isPasswordsNotEqual(RegistrationUserDto registrationUserDto) {
        return !registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword());
    }
}

