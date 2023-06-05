package cs27.educate.howareyou.domain.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 体調記録のエンティティ
 * ユーザを参照する
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    // 体調ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskid;

    // ユーザID
    private String uid;

    // タスク名
    private String taskname;

    // タスク度
    private int taskfrequency;

    // タスク進捗具合
    private int taskshintyoku;

    // コメント
    private String comment;

}
