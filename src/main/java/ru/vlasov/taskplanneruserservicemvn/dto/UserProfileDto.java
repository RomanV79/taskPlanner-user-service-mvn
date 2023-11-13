package ru.vlasov.taskplanneruserservicemvn.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserProfileDto {
    private String name;
    private String email;
    private LocalDate registrationDate;
}
