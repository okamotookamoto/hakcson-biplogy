package cs27.educate.howareyou.application.controller;

import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * コントローラ(Thymeleaf)の結合テスト例
 *
 * UserController + UserService + UserRepository + 実際のDB
 *
 * サーバーは起動しない
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class UserControllerIntegrationTests {

  /*-- テストで使用するデータ --*/
  private final String uid = "hoge";
  private final String nickname = "ほげ";
  private final String email = "hoge@example.com";

  /*-- テストで使用するBean --*/
  // 疑似HTTPリクエストを行う
  @Autowired
  private MockMvc mvc;
  // JPA処理を行う
  @Autowired
  private TestEntityManager entityManager;
  // DB検証用リポジトリ
  @Autowired
  private UserRepository users;

  /*-- テスト処理 --*/
  @BeforeEach
  void beforeEach() {

    entityManager.clear();

  }

  @Nested
  class SearchUserInformationTests {

    // ユーザの取得に成功
    @Test
    void searchUserInformationSuccess() throws Exception {

      // 前処理としてBDにユーザを追加
      entityManager.persist(new User(
          uid,
          nickname,
          email));

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/user/information").param(
              "uid",
              uid))
          // 結果を検証
          .andExpect(status().isOk())
          .andExpect(view().name("information"))
          .andExpect(model().attribute(
              "uid",
              uid))
          .andExpect(model().attribute(
              "nickname",
              nickname))
          .andExpect(model().attribute(
              "email",
              email));

    }

    // ユーザが存在しない場合
    @Test
    void searchUserInformationUserDoesNotExist() throws Exception {

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/user/information").param(
              "uid",
              uid))
          // 結果の検証
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/"))
          .andExpect(flash().attribute(
              "isUserDoesNotExistError",
              true));

    }

    // ユーザIDが正規表現に一致しない場合
    @Test
    void searchUserInformationInvalidUid() throws Exception {

      final String invalidUid = "@abc";

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/user/information").param(
              "uid",
              invalidUid))
          // 結果の検証
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/"))
          .andExpect(flash().attribute(
              "isUidValidationError",
              true));

    }

    // パラメータがnullであった場合
    @Test
    void searchUserInformationNullUid() throws Exception {

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/user/information"))
          // 結果の検証
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/"))
          .andExpect(flash().attribute(
              "isUidValidationError",
              true));

    }

  }

  @Nested
  class RegisterUserTests {

    // ユーザの登録に成功
    @Test
    void registerUserSuccess() throws Exception {

      // 疑似HTTPリクエスト
      mvc
          .perform(post("/user/register")
                       .param(
                           "uid",
                           uid)
                       .param(
                           "nickname",
                           nickname)
                       .param(
                           "email",
                           email))
          // 結果の検証
          .andExpect(status().isOk())
          .andExpect(view().name("input"))
          .andExpect(model().attribute(
              "uid",
              uid))
          .andExpect(model().attribute(
              "nickname",
              nickname))
          .andExpect(model().attributeExists("healthConditionForm"));

      // DB内を検証
      assertThat(users.existsById(uid)).isTrue();
    }

    // ユーザIDが正規表現に一致しない場合
    @Test
    void registerUserInvalidForm() throws Exception {

      final String invalidUid = "@abc";

      // 疑似HTTPリクエスト
      mvc
          .perform(post("/user/register")
                       .param(
                           "uid",
                           invalidUid)
                       .param(
                           "nickname",
                           nickname)
                       .param(
                           "email",
                           email))
          // 結果の検証
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/user/signup"))
          .andExpect(flash().attribute(
              "isUserFormError",
              true));

    }

    // ユーザが登録済みであった場合
    @Test
    void registerUserAlreadyExists() throws Exception {

      // 前処理としてBDにユーザを追加
      entityManager.persist(new User(
          uid,
          nickname,
          email));

      // 疑似HTTPリクエスト
      mvc
          .perform(post("/user/register")
                       .param(
                           "uid",
                           uid)
                       .param(
                           "nickname",
                           nickname)
                       .param(
                           "email",
                           email))
          // 結果の検証
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("/user/signup"))
          .andExpect(flash().attribute(
              "isUserAlreadyExistsError",
              true));

    }

  }

}
