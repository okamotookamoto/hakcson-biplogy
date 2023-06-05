package cs27.educate.howareyou.domain.repository;

import cs27.educate.howareyou.domain.entity.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    // すべてのUserエンティティのデータを取得するためのメソッド
    List<User> findAll();
}
