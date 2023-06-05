package cs27.educate.howareyou.domain.repository;

import cs27.educate.howareyou.domain.entity.Task;
import cs27.educate.howareyou.domain.entity.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface TaskRepository extends CrudRepository<User, String> {
    Task findByUid(String uid);

    List<Task> findByTeamid(String teamid);
}
