package cs27.educate.howareyou.domain.service;

import cs27.educate.howareyou.application.form.HealthConditionForm;
import cs27.educate.howareyou.application.form.HealthQueryForm;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.dto.HealthQueryResult;
import cs27.educate.howareyou.domain.entity.HealthCondition;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.HealthConditionRepository;
import cs27.educate.howareyou.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;

/**
 * 体調記録・レビューのロジックを提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class HealthConditionService {

  private final UserRepository users;
  private final HealthConditionRepository conditions;
  private final UserService userService;

  /**
   * 体調を記録する
   *
   * @param form HealthConditionForm
   * @return 記録した体調データ
   */
  public HealthCondition record(
      HealthConditionForm form) {

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが登録されていない場合エラーを返す
    if (!users.existsById(uid)) {
      throw new UserValidationException(
          USER_DOES_NOT_EXIST,
          "record the health condition",
          String.format(
              "user %s does not exist",
              uid));
    }

    // 体調の記録をDBに保存し、保存した記録を戻り値として返す
    return conditions.save(new HealthCondition(
        null,
        null,
        form.getScore(),
        form.getComment(),
        new Timestamp(System.currentTimeMillis())));
  }

  /**
   * 体調を日付、キーワードで検索する
   *
   * @param form HealthQueryForm
   * @return 検索結果
   */
  public HealthQueryResult query(HealthQueryForm form) {

    // フォームの中身を変数に格納する
    final String uid = form.getUid();
    final LocalDate since = form.getSince();
    final LocalDate until = form.getUntil();
    final String keyword = form.getKeyword();

    // ユーザ情報を取得し、ニックネームを変数に格納する
    // ユーザが登録済みかどうかの確認も兼ねている
    final String nickname = userService
        .getUser(uid)
        .getNickname();

    // 検索キーワードがnullであった場合、部分一致検索で全ての結果を取得するために検索キーワードを""に変更する
    final String containingWord = keyword == null ? "" : keyword;

    List<HealthCondition> conditionList;

    // DBから体調記録を検索する

    if (since != null && until != null) { // sinceとuntilがともに指定されていた場合

      conditionList = conditions.findByUidAndRecordedOnBetweenAndCommentContainingOrderByRecordedOnDesc(
          uid,
          // Timestamp型に変換
          Timestamp.valueOf(since.atStartOfDay()),
          // 終了日の24:00までに変更し、Timestamp型に変換
          Timestamp.valueOf(until.plusDays(1).atStartOfDay()),
          containingWord);

    } else if (since == null && until != null) { // untilのみ指定されていた場合

      conditionList = conditions.findByUidAndRecordedOnLessThanEqualAndCommentContainingOrderByRecordedOnDesc(
          uid,
          Timestamp.valueOf(until.plusDays(1).atStartOfDay()),
          containingWord);

    } else if (since != null) { // sinceのみ指定されていた場合

      conditionList = conditions.findByUidAndRecordedOnGreaterThanEqualAndCommentContainingOrderByRecordedOnDesc(
          uid,
          Timestamp.valueOf(since.atStartOfDay()),
          containingWord);

    } else { // sinceもuntilも未指定の場合

      conditionList = conditions.findByUidAndCommentContainingOrderByRecordedOnDesc(
          uid,
          containingWord);

    }

    // 検索結果を返す
    return new HealthQueryResult(
        uid,
        nickname,
        since,
        until,
        keyword,
        conditionList);

  }

}
