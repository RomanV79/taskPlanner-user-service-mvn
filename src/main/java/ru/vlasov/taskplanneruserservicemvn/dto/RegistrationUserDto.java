package ru.vlasov.taskplanneruserservicemvn.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RegistrationUserDto {
    private String name;
    private String username;
    private String password;
    private String confirmPassword;

}
