package ru.vlasov.taskplanneruserservicemvn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlasov.taskplanneruserservicemvn.entity.AppUser;
import ru.vlasov.taskplanneruserservicemvn.entity.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<List<Task>> findAllByAppUser(AppUser appUser);
    Optional<Task> findByAppUserAndId(AppUser appUser, Long id);
}
