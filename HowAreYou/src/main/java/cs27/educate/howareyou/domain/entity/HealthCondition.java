package cs27.educate.howareyou.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 体調記録のエンティティ
 * ユーザを参照する
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthCondition {

  // 体調ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cid;

  // ユーザID
  private String uid;

  // 体調スコア
  private int score;

  // コメント
  private String comment;

  // 記録日時
  // @Temporal(TemporalType.TIMESTAMP)
  private Timestamp recordedOn;

}
