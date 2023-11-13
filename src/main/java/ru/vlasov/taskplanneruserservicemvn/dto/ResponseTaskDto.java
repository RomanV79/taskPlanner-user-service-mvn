package ru.vlasov.taskplanneruserservicemvn.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ResponseTaskDto {

    private long id;
    private String created;
    private String title;
    private String description;
    private boolean isDone;
}
