package ru.vlasov.taskplanneruserservicemvn.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.dto.RequestTaskDto;
import ru.vlasov.taskplanneruserservicemvn.dto.ResponseTaskDto;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.Task;
import ru.vlasov.taskplanneruserservicemvn.entity.TaskStatus;
import ru.vlasov.taskplanneruserservicemvn.repository.AppUserRepository;
import ru.vlasov.taskplanneruserservicemvn.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;
    private final ModelMapper modelMapper;
    private final ConverterDtoService converterDtoService;

    public List<ResponseTaskDto> getTasksByEmail(String email) {

        AppUser appUser = appUserRepository.findByEmail(email).get();
        List<Task> tasks = taskRepository.findAllByAppUser(appUser).orElse(new ArrayList<>());
        List<ResponseTaskDto> responseTaskDtoList = new ArrayList<>();
        if (!tasks.isEmpty()) {
            responseTaskDtoList = tasks.stream().map(converterDtoService::doFromTaskToTaskDto).collect(Collectors.toList());
        }
        return responseTaskDtoList;
    }


    public ResponseTaskDto getTaskByIdAndEmail(long id, String email) {

        AppUser appUser = appUserRepository.findByEmail(email).orElseThrow();

        Task task = taskRepository.findByAppUserAndId(appUser, id).orElse(null);
        if (task == null) {
            return new ResponseTaskDto();
        }
        return converterDtoService.doFromTaskToTaskDto(task);
    }

    public boolean save(String email, RequestTaskDto requestTaskDto) {
        AppUser appUser = appUserRepository.findByEmail(email).orElse(null);
        if (appUser == null || requestTaskDto.getTitle() == null) {
            return false;
        }

        Task task;
        if (requestTaskDto.getId() == null) {
            task = Task.builder()
                    .appUser(appUser)
                    .title(requestTaskDto.getTitle())
                    .description(requestTaskDto.getDescription())
                    .taskStatus(requestTaskDto.getStatus() == null ? TaskStatus.NEW : TaskStatus.DONE)
                    .build();
            log.info("created task -> {}", task.toString());
        } else {
            task = taskRepository.findByAppUserAndId(appUser,requestTaskDto.getId()).orElse(null);
            if (task == null) {
                return false;
            }
            task.setTitle(requestTaskDto.getTitle());
            task.setDescription(requestTaskDto.getDescription());

            if (requestTaskDto.getStatus() == null) {
                task.setTaskStatus(TaskStatus.NEW);
                task.setFinished(null);
            } else if (task.getTaskStatus() != TaskStatus.DONE) {
                task.setTaskStatus(TaskStatus.DONE);
                task.setFinished(LocalDate.now());
            }
        }

        taskRepository.save(task);
        return true;
    }

    public boolean delete(String email, long id) {
        AppUser appUser = appUserRepository.findByEmail(email).orElse(null);
        if (appUser == null) {
            return false;
        }
        Task task = taskRepository.findByAppUserAndId(appUser, id).orElse(null);
        if (task == null) {
            return false;
        }
        taskRepository.delete(task);

        return true;
    }
}

