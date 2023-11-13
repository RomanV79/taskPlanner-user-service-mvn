package ru.vlasov.taskplanneruserservicemvn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class AuthUserDto {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "AuthUserDto{" +
                "email='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
