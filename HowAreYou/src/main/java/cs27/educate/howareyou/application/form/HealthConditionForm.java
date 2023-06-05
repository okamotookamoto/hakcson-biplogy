package cs27.educate.howareyou.application.form;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 体調を記録するフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthConditionForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

  // 体調のスコア
  @Min(0)
  @Max(100)
  private int score;

  // コメント
  private String comment;

}
