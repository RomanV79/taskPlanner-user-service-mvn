package ru.vlasov.taskplanneruserservicemvn.exception.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppError {
    private int status;
    private ErrMessage message;
}
