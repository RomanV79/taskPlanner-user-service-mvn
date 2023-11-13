package ru.vlasov.taskplanneruserservicemvn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class RequestTaskDto {
    private Long id;
    private String title;
    private String description;
    private String status;
}
