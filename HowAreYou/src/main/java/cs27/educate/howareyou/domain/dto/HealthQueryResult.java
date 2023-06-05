package cs27.educate.howareyou.domain.dto;

import cs27.educate.howareyou.domain.entity.HealthCondition;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 体調記録の検索結果
 */
@Data
public class HealthQueryResult {

  // ユーザID
  private final String uid;
  // ニックネーム
  private final String nickname;
  // 検索の開始日
  private final LocalDate since;
  // 検索の終了日
  private final LocalDate until;
  // 検索キーワード
  private final String keyword;
  // 検索結果のリスト
  private final List<HealthCondition> logs;

}
