package cs27.educate.howareyou.domain.repository;

import cs27.educate.howareyou.domain.entity.HealthCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * リポジトリ結合テスト例
 *
 * HealthConditionRepositoryを実際のDBでテスト
 * 自作メソッドの動作を検証する
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HealthConditionRepositoryIntegrationTests {

  /*-- テストで使用するデータ --*/
  // UIDがhogeの場合にrecordedOn降順だと、インデックスは 4,2,0,1 の順になる
  private final List<String> uids = Arrays.asList(
      "hoge",
      "hoge",
      "hoge",
      "fuga",
      "hoge");
  private final List<Integer> scores = Arrays.asList(
      10,
      97,
      55,
      0,
      33);
  private final List<String> comments = Arrays.asList(
      "体調が悪いです",
      "元気です",
      "普通です",
      "",
      "あまり元気がないです");
  private final List<Timestamp> recordedOns = Arrays.asList(
      Timestamp.valueOf("2021-01-01 01:02:03"),
      Timestamp.valueOf("2020-01-01 01:02:03"),
      Timestamp.valueOf("2021-01-01 23:59:59"),
      Timestamp.valueOf("2021-03-31 01:02:03"),
      Timestamp.valueOf("2021-03-31 01:02:03"));

  /*-- テストで使用するBean --*/
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private HealthConditionRepository conditions;

  // 各テストの前処理
  @BeforeEach
  void beforeEach() {

    entityManager.clear();

    IntStream
        .range(
            0,
            uids.size())
        .forEach(it -> entityManager.persist(new HealthCondition(
            null,
            uids.get(it),
            scores.get(it),
            comments.get(it),
            recordedOns.get(it))));
  }

  @Test
  void deleteByUid() {

    assertThat(conditions.count()).isEqualTo(uids.size());

    conditions.deleteByUid(uids.get(0));

    assertThat(conditions.count()).isEqualTo(1);

  }

  @Test
  void findByUidAll() {

    // 日時降順
    assertListIndexes(
        conditions.findByUidAndCommentContainingOrderByRecordedOnDesc(
            uids.get(0),
            ""),
        Arrays.asList(
            4,
            2,
            0,
            1));
  }

  @Test
  void findByUidKeyword() {

    // 日時降順 & コメントに"元気"の文字が入っている
    assertListIndexes(
        conditions.findByUidAndCommentContainingOrderByRecordedOnDesc(
            uids.get(0),
            "元気"),
        Arrays.asList(
            4,
            1));
  }

  @Test
  void findByUidUntil() {

    // 2021-01-01の終日までの記録を日時で降順
    assertListIndexes(
        conditions.findByUidAndRecordedOnLessThanEqualAndCommentContainingOrderByRecordedOnDesc(
            uids.get(0),
            Timestamp.valueOf("2021-01-02 00:00:00"),
            ""),
        Arrays.asList(
            2,
            0,
            1));
  }

  @Test
  void findByUidSince() {

    // 2021-01-01以降の記録を日時で降順
    assertListIndexes(
        conditions.findByUidAndRecordedOnGreaterThanEqualAndCommentContainingOrderByRecordedOnDesc(
            uids.get(0),
            Timestamp.valueOf("2021-01-01 00:00:00"),
            ""),
        Arrays.asList(
            4,
            2,
            0));
  }

  @Test
  void findByUidBetween() {

    // 2021-01-01の記録を日時で降順
    assertListIndexes(
        conditions.findByUidAndRecordedOnBetweenAndCommentContainingOrderByRecordedOnDesc(
            uids.get(0),
            Timestamp.valueOf("2021-01-01 00:00:00"),
            Timestamp.valueOf("2021-01-02 00:00:00"),
            ""),
        Arrays.asList(
            2,
            0));
  }

  // リポジトリから取得したリストと、正しい処理が行われた場合のリストの、インデックスを比較検証する
  private void assertListIndexes(
      List<HealthCondition> conditionList,
      List<Integer> correctIndexes) {

    IntStream
        .range(
            0,
            correctIndexes.size())
        .forEach(it -> assertThat(conditionList
                                      .get(it)
                                      .getScore()).isEqualTo(scores.get(correctIndexes.get(it))));
  }

}
