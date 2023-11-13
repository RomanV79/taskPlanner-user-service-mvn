package ru.vlasov.taskplanneruserservicemvn.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.vlasov.taskplanneruserservicemvn.exception.ConfirmationNotSuccessfullyException;
import ru.vlasov.taskplanneruserservicemvn.exception.PasswordsNotSameException;
import ru.vlasov.taskplanneruserservicemvn.exception.UserAlreadyExistException;
import ru.vlasov.taskplanneruserservicemvn.exception.UserNotAuthenticated;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppError> handleAuthenticationException(Exception ex) {
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), ErrMessage.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<AppError> handleUserAlreadyExist() {
        return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(), ErrMessage.USER_ALREADY_EXIST), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordsNotSameException.class)
    public ResponseEntity<AppError> handlePasswordsNotSame() {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), ErrMessage.PASSWORDS_ARE_NOT_EQUAL), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<AppError> badCredentialsException() {
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), ErrMessage.CREDENTIALS_IS_NOT_VALID), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<AppError> notAuthenticated() {
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), ErrMessage.JWT_TOKEN_NOT_VALID), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConfirmationNotSuccessfullyException.class)
    public ResponseEntity<AppError> notConfirmed() {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), ErrMessage.CONFIRMATION_TOKEN_NOT_VALID), HttpStatus.BAD_REQUEST);
    }
}
