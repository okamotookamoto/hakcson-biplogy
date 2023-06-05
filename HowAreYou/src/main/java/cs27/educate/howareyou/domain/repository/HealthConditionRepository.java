package cs27.educate.howareyou.domain.repository;

import cs27.educate.howareyou.domain.entity.HealthCondition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 体調記録のリポジトリ
 */
@Repository
public interface HealthConditionRepository
    extends CrudRepository<HealthCondition, Long> {

  //List<HealthCondition> findByUidOrderByRecordedOnDesc(String uid);

  /**
   * ユーザIDが一致する体調記録を全て削除する
   *
   * @param uid ユーザID
   */
  void deleteByUid(String uid);

  /**
   * ユーザIDが一致し、指定のキーワードを含む体調記録を、記録日時の新しい順に並び替えて取得する
   *
   * @param uid ユーザID
   * @param containingWord 検索キーワード
   * @return 検索結果
   */
  List<HealthCondition> findByUidAndCommentContainingOrderByRecordedOnDesc(
      String uid,
      String containingWord);

  /**
   * ユーザIDが一致し、指定のキーワードを含み、ある日付以前に記録された体調記録を、記録日時の新しい順に並び替えて取得する
   *
   * @param uid ユーザID
   * @param until 検索の終了日
   * @param containingWord 検索キーワード
   * @return 検索結果
   */
  List<HealthCondition> findByUidAndRecordedOnLessThanEqualAndCommentContainingOrderByRecordedOnDesc(
      String uid,
      Timestamp until,
      String containingWord);

  /**
   * ユーザIDが一致し、指定のキーワードを含み、ある日付以降に記録された体調記録を、記録日時の新しい順に並び替えて取得する
   *
   * @param uid ユーザID
   * @param since 検索の開始日
   * @param containingWord 検索キーワード
   * @return 検索結果
   */
  List<HealthCondition> findByUidAndRecordedOnGreaterThanEqualAndCommentContainingOrderByRecordedOnDesc(
      String uid,
      Timestamp since,
      String containingWord);

  /**
   * ユーザIDが一致し、指定のキーワードを含み、ある期間に記録された体調記録を、記録日時の新しい順に並び替えて取得する
   *
   * @param uid ユーザID
   * @param since 検索の開始日
   * @param until 検索の終了日
   * @param containingWord 検索キーワード
   * @return 検索結果
   */
  List<HealthCondition> findByUidAndRecordedOnBetweenAndCommentContainingOrderByRecordedOnDesc(
      String uid,
      Timestamp since,
      Timestamp until,
      String containingWord);

}
