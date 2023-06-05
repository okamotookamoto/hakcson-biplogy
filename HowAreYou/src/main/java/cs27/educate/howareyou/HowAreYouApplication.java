package cs27.educate.howareyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Bootアプリケーションの起動クラス
 * メソッドしてメイン関数を含む
 *
 * Auto-configurationとComponent Scanを行い、Beanを構成する
 */
@SpringBootApplication
public class HowAreYouApplication {

  /**
   * メイン関数
   *
   * Spring Bootアプリケーションを起動し、起点となる設定クラスを指定する
   *
   * @param args コマンドライン引数
   */
  public static void main(String[] args) {

    SpringApplication.run(
        HowAreYouApplication.class,
        args);
  }

}
