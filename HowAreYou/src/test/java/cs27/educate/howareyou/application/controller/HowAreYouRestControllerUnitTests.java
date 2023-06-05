package cs27.educate.howareyou.application.controller;

import cs27.educate.howareyou.application.controller.rest.HowAreYouRestController;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.service.HealthConditionService;
import cs27.educate.howareyou.domain.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static cs27.educate.howareyou.configuration.exception.CodeString.*;
import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RestControllerの単体テスト例
 *
 * HowAreYouRestControllerのみをテストするため、サービスはモックにする
 *
 * サーバーは起動しない
 *
 */
// HowAreYouRestControllerにモックを注入する
@WebMvcTest(HowAreYouRestController.class)
public class HowAreYouRestControllerUnitTests {

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
  private UserService userService;
  // モックにする
  @MockBean
  private HealthConditionService conditionService;

  /*-- テスト処理 --*/
  @Nested
  class SearchUserTests {

    // ユーザの検索に成功した場合
    @Test
    void searchUserSuccess() throws Exception {

      // モックの処理を定義
      given(userService.getUser(uid)).willReturn(new User(
          uid,
          nickname,
          email));

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/api/user").param(
              "uid",
              uid))
          // 結果の検証
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.result.uid").value(uid))
          .andExpect(jsonPath("$.result.nickname").value(nickname))
          .andExpect(jsonPath("$.result.email").value(email));

    }

    // ユーザIDが空白であった場合
    @Test
    void searchUserBlank() throws Exception {

      final String blank = " ";

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/api/user").param(
              "uid",
              blank))
          // 結果の検証
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code").value(
              ERROR_PREFIX.getStr() + CONTROLLER_VALIDATION_ERROR.getCode()));

    }

    // ユーザが存在しない場合
    @Test
    void searchUserDoesNotExist() throws Exception {

      // モックの処理を定義
      given(userService.getUser(uid)).willThrow(new UserValidationException(
          USER_DOES_NOT_EXIST,
          "",
          ""));

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/api/user").param(
              "uid",
              uid))
          // 結果の検証
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code").value(
              ERROR_PREFIX.getStr() + USER_DOES_NOT_EXIST.getCode()));
    }

  }

}
