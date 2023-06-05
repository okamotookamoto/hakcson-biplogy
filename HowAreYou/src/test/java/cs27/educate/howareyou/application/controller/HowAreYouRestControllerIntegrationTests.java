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

import static cs27.educate.howareyou.configuration.exception.CodeString.*;
import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RestControllerの結合テスト例
 *
 * HowAreYouRestController + UserService + UserRepository + 実際のDB
 *
 * サーバーは起動しない
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class HowAreYouRestControllerIntegrationTests {

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
  class SearchUserTests {

    // ユーザの取得に成功
    @Test
    void searchUserSuccess() throws Exception {

      // 前処理としてDBにユーザを追加
      entityManager.persist(new User(
          uid,
          nickname,
          email));

      // 疑似HTTPリクエスト
      mvc
          .perform(get("/api/user").param(
              "uid",
              uid))
          // 結果を検証
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
