package cs27.educate.howareyou.application.form;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 体調記録を検索するフォーム
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthQueryForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

  // 検索の開始日
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate since;

  // 検索の終了日
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate until;

  // 検索キーワード
  private String keyword;

}
