package ru.vlasov.taskplanneruserservicemvn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class TaskPlannerUserServiceMvnApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskPlannerUserServiceMvnApplication.class, args);
    }

}
