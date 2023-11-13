package ru.vlasov.taskplanneruserservicemvn.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.dto.MessageDto;
import ru.vlasov.taskplanneruserservicemvn.dto.ResponseTaskDto;
import ru.vlasov.taskplanneruserservicemvn.dto.UserProfileDto;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.ConfirmationToken;
import ru.vlasov.taskplanneruserservicemvn.entity.Task;
import ru.vlasov.taskplanneruserservicemvn.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterDtoService {

    @Value("${app.public-host}")
    private String publicHost;

    private final ModelMapper modelMapper;

    public UserProfileDto doFromUserToUserProfile(AppUser appUser) {

        TypeMap<AppUser, UserProfileDto> typeMap = modelMapper.typeMap(AppUser.class, UserProfileDto.class);
        Converter<Long, LocalDate> toLocalDate = new AbstractConverter<Long, LocalDate>() {
            @Override
            protected LocalDate convert(Long source) {
                LocalDate date =
                        Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDate();
                return date;
            }
        };
        typeMap.addMappings(mapping -> mapping.using(toLocalDate).map(AppUser::getCreated, UserProfileDto::setRegistrationDate));

        return modelMapper.map(appUser, UserProfileDto.class);
    }

    public ResponseTaskDto doFromTaskToTaskDto(Task task) {

        TypeMap<Task, ResponseTaskDto> typeMap = modelMapper.typeMap(Task.class, ResponseTaskDto.class);

        Converter<Long, String> toLocalDate = new AbstractConverter<Long, String>() {
            @Override
            protected String convert(Long source) {
                LocalDateTime dateTime =
                        Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return dateTime.format(formatter);
            }
        };
        Converter<TaskStatus, Boolean> isDone = new AbstractConverter<TaskStatus, Boolean>() {
            protected Boolean convert(TaskStatus taskStatus) {
                return taskStatus == TaskStatus.DONE;
            }
        };


        typeMap.addMappings(mapping -> mapping.using(toLocalDate).map(Task::getCreated, ResponseTaskDto::setCreated));
        typeMap.addMappings(mapping -> mapping.using(isDone).map(Task::getTaskStatus, ResponseTaskDto::setDone));

        return modelMapper.map(task, ResponseTaskDto.class);
    }

    public MessageDto createConfirmMessageFromConfirmationToken(ConfirmationToken confirmationToken) {
        var title = String.format("Confirm registration on %s", publicHost);
        var body = String.format("To confirm the email, please follow the link from the email <a href=\"%s/?confirm-token=%s\">%s/?confirm-token=%s</a>",
                publicHost, confirmationToken.getToken(), publicHost, confirmationToken.getToken());
        return MessageDto.builder()
                .email(confirmationToken.getAppUser().getEmail())
                .title(title)
                .body(body)
                .build();
    }
}
