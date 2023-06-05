package cs27.educate.howareyou.application.controller.rest;

import cs27.educate.howareyou.application.form.HealthQueryForm;
import cs27.educate.howareyou.configuration.exception.Response;
import cs27.educate.howareyou.configuration.exception.ResponseCreator;
import cs27.educate.howareyou.domain.dto.HealthQueryResult;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.service.HealthConditionService;
import cs27.educate.howareyou.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 体調記録・レビューを行うAPIを提供するコントローラークラス
 * 成功時、エラー時ともにResponse型で値を返す
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class HowAreYouRestController {

  private final HealthConditionService conditionService;
  private final UserService userService;

  /**
   * 体調を日付、キーワードで検索する
   *
   * @param uid     ユーザID
   * @param since   検索開始日
   * @param until   検索終了日
   * @param keyword 検索キーワード
   * @return 体調の検索結果
   */
  @GetMapping("/condition")
  public Response<HealthQueryResult> searchCondition(
      @RequestParam("uid")
      @Pattern(regexp = "[0-9a-zA-Z_\\-]+") String uid,
      @RequestParam(value = "since", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate since,
      @RequestParam(value = "until", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate until,
      @RequestParam(value = "keyword", required = false)
          String keyword) {

    // 体調を検索し、結果をResponse型でラップして返す
    return ResponseCreator.succeed(conditionService.query(new HealthQueryForm(
        uid,
        since,
        until,
        keyword)));

  }

  /**
   * ユーザを検索する
   *
   * @param uid ユーザID
   * @return ユーザ情報
   */
  @GetMapping("/user")
  public Response<User> searchUser(
      @RequestParam("uid")
      @Pattern(regexp = "[0-9a-zA-Z_\\-]+") String uid) {

    // ユーザを検索し、結果をResponse型でラップして返す
    return ResponseCreator.succeed(userService.getUser(uid));

  }

}
