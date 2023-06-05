package cs27.educate.howareyou.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * ユーザエンティティ
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  // ユーザID
  @Id
  private String uid;

  // ニックネーム
  private String nickname;

  // チームID
  private String teamid;

  // ステータス(順調，ぼちぼち，助けてほしい)
  private int status;

  // コメント（自由記述）
  private String support;

  // 何回助けたのか
  private int help;
}
