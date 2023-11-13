package ru.vlasov.taskplanneruserservicemvn.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlasov.taskplanneruserservicemvn.dto.RequestTaskDto;
import ru.vlasov.taskplanneruserservicemvn.exception.UserNotAuthenticated;
import ru.vlasov.taskplanneruserservicemvn.exception.error.AppError;
import ru.vlasov.taskplanneruserservicemvn.exception.error.ErrMessage;
import ru.vlasov.taskplanneruserservicemvn.service.TaskService;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@RestController
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAll(Principal principal) {
        String email = getEmailIfAutenticated(principal);

        return ResponseEntity.ok(taskService.getTasksByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Principal principal, @PathVariable long id) {
        String email = getEmailIfAutenticated(principal);

        return ResponseEntity.ok(taskService.getTaskByIdAndEmail(id, email));
    }

    @PostMapping
    public ResponseEntity<?> create(Principal principal, @RequestBody RequestTaskDto requestTaskDto) {
        String email = getEmailIfAutenticated(principal);

        if (!taskService.save(email, requestTaskDto)) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), ErrMessage.COULD_NOT_CREATE_ENTITY));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @PutMapping
    public ResponseEntity<?> update(Principal principal, @RequestBody RequestTaskDto requestTaskDto) {
        String email = getEmailIfAutenticated(principal);

        if (!taskService.save(email, requestTaskDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), ErrMessage.COULD_NOT_UPDATE_ENTITY));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable long id) {
        String email = getEmailIfAutenticated(principal);

        if (!taskService.delete(email, id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), ErrMessage.COULD_NOT_DELETE_ENTITY));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    private static String getEmailIfAutenticated(Principal principal) {
        if (principal == null) {
            throw new UserNotAuthenticated();
        }
        return principal.getName();
    }
}
