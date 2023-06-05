package cs27.educate.howareyou.domain.service;

import cs27.educate.howareyou.application.form.UserForm;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * サービス単体テスト例
 *
 * UserServiceの動作のみをテストするため、UserRepositoryはモックにする
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

  /*-- テストで使用するデータ --*/
  private final String uid = "hoge";
  private final String nickname = "ほげ";
  private final String email = "hoge@example.com";

  /*-- テストで使用するBean --*/
  // リポジトリをモックにする
  @Mock
  private UserRepository users;
  // テスト対象のサービスにモックを注入する
  @InjectMocks
  private UserService service;

  /*-- テスト処理 --*/
  @Nested
  class CreateUserTests {

    // ユーザ作成に成功した場合
    @Test
    void createUserSuccess() {

      // モックの処理を定義
      given(users.existsById(uid)).willReturn(false);
      given(users.save(any(User.class))).willReturn(new User(
          uid,
          nickname,
          email));

      // 処理を実行
      final User returnUser = service.createUser(new UserForm(
          uid,
          nickname,
          email));

      // 結果を検証
      assertThat(returnUser.getUid()).isEqualTo(uid);
      assertThat(returnUser.getNickname()).isEqualTo(nickname);
      assertThat(returnUser.getEmail()).isEqualTo(email);

    }

    // ユーザが作成済みであた場合
    @Test
    void createUserAlreadyExists() {

      // モックの処理を定義
      given(users.existsById(uid)).willReturn(true);

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
