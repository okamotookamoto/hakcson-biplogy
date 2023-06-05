package cs27.educate.howareyou.domain.service;

import cs27.educate.howareyou.application.form.UserForm;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

/**
 * サービス結合テスト例
 *
 * UserService + UserRepository + 実際のDB
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserService.class)
public class UserServiceIntegrationTests {

  /*-- テストで使用するデータ --*/
  private final String uid = "hoge";
  private final String nickname = "ほげ";
  private final String email = "hoge@example.com";

  /*-- テストで使用するBean --*/
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private UserRepository users;
  @Autowired
  private UserService service;

  /*-- テスト処理 --*/
  @BeforeEach
  void beforeEach() {

    entityManager.clear();
  }

  @Nested
  class CreateUserTests {

    // ユーザ作成に成功した場合
    @Test
    void createUserSuccess() {

      // 処理を実行
      final User returnUser = service.createUser(new UserForm(
          uid,
          nickname,
          email));

      // DB内を検証
      assertThat(users.existsById(uid)).isTrue();

      // 戻り値を検証
      assertThat(returnUser.getUid()).isEqualTo(uid);
      assertThat(returnUser.getNickname()).isEqualTo(nickname);
      assertThat(returnUser.getEmail()).isEqualTo(email);

    }

    // ユーザが作成済みであった場合
    @Test
    void createUserAlreadyExists() {

      // 前処理としてDBにユーザを追加しておく
      entityManager.persist(new User(
          uid,
          nickname,
          email));

      // 処理の実行と結果の検証
      assertThatThrownBy(() -> service.createUser(new UserForm(
          uid,
          nickname,
          email))).isInstanceOfSatisfying(
          UserValidationException.class,
          (e) -> assertThat(e.getCode()).isEqualTo(USER_ALREADY_EXISTS));

    }

  }

}
