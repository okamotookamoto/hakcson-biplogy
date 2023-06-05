package cs27.educate.howareyou.application.form;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * ユーザ登録・更新フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

  // ニックネーム
  @NotBlank
  private String nickname;

  // チーム名
  @NotBlank
  private String teamid;

  // ステータス名
  private int status;
  // 進行中かどうか
  private String support;

  // 何回助けたのか
  private int help;

}
