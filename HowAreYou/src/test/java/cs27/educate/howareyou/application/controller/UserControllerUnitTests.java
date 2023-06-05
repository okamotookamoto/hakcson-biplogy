package cs27.educate.howareyou.application.controller;

import cs27.educate.howareyou.application.controller.view.UserController;
import cs27.educate.howareyou.application.form.UserForm;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * コントローラ(Thymeleaf)の単体テスト例
 *
 * UserControllerのみのテストのため、UserServiceはモックにする
 *
 * サーバーは起動しない
 *
 */
// UserControllerにモックを注入する
@WebMvcTest(UserController.class)
public class UserControllerUnitTests {

  /*-- テストで使用するデータ --*/
  private final String uid = "hoge";
  private final String nickname = "ほげ";
  private final String email = "hoge@example.com";

  /*-- テストで使用するBean --*/
  // 疑似HTTPリクエストを行う
  @Autowired
  private MockMvc mvc;
  // モックにする
  @MockBean
  private UserService service;

  /*-- テスト処理 --*/
  @Nested
  class SearchUserInformationTests {

    // ユーザ情報の取得に成功
    @Test
    void searchUserInformationSuccess() throws Exception {

      // モックの処理を定義
      given(service.getUser(uid)).willReturn(new User(
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

      // モックの処理を定義
      given(service.getUser(uid)).willThrow(new UserValidationException(
          USER_DOES_NOT_EXIST,
          "",
          ""));

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/user/information").param(
              "uid",
              uid))
          // 結果を検証
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

      // モックの処理を定義
      given(service.createUser(any(UserForm.class))).willReturn(new User(
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
          .andExpect(status().isOk())
          .andExpect(view().name("input"))
          .andExpect(model().attribute(
              "uid",
              uid))
          .andExpect(model().attribute(
              "nickname",
              nickname))
          .andExpect(model().attributeExists("healthConditionForm"));

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

      // モックの処理を定義
      given(service.createUser(any(UserForm.class))).willThrow(new UserValidationException(
          USER_ALREADY_EXISTS,
          "",
          ""));

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
