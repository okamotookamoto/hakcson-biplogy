package cs27.educate.howareyou.application.controller.view;

import cs27.educate.howareyou.application.form.HealthConditionForm;
import cs27.educate.howareyou.application.form.HealthQueryForm;
import cs27.educate.howareyou.domain.service.HealthConditionService;
import cs27.educate.howareyou.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 体調記録・レビューを行うコントローラークラス
 * Thymeleafに渡す値をModelに登録し、ページの表示先にリダイレクトする
 */
@Controller
@RequiredArgsConstructor
public class HealthConditionController {

  private final HealthConditionService conditionService;
  private final UserService userService;

  /**
   * 体調を記録する
   *
   * @param attributes
   * @param form HealthConditionForm
   * @return 体調記録ページ
   */
  @PostMapping("/condition/input")
  public String inputHealathCondition(
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          HealthConditionForm form,
      BindingResult bindingResult) {

    // ユーザIDに使用できない文字が含まれていた場合
    if(bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))){
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUidValidationError",
          true);

      // 初期画面に戻る
      return "redirect:/";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザIDとニックネームをModelに追加する
    attributes.addFlashAttribute(
        "uid",
        uid);
    attributes.addFlashAttribute(
        "nickname",
        userService
            .getUser(uid)
            .getNickname());

    // スコアにエラーが含まれていた場合
    if(bindingResult.hasErrors()){
      // エラーフラグをオンにする
      attributes.addFlashAttribute("isHealthConditionFormError", true);

      // 体調入力ページ
      return "redirect:/howareyou";
    }

    // 体調を記録する
    conditionService.record(form);

    // 体調記録ページ
    return "redirect:/howareyou";
  }

  /**
   * 体調を日付、キーワードで検索する
   *
   * @param model
   * @param attributes
   * @param form HealthQueryForm
   * @return 体調振り返りページ
   */
  @GetMapping("/condition")
  public String serachCondition(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          HealthQueryForm form,
      BindingResult bindingResult) {

    // フォームのバリデーション違反があった場合
    if(bindingResult.hasErrors()){
      // ユーザIDに使用できない文字が含まれていた場合
      if(bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))){
        // エラーフラグをオンにする
        attributes.addFlashAttribute(
            "isUidValidationError",
            true);

        // 初期画面に戻る
        return "redirect:/";
      }

      // 日付のエラーがあった場合

      // エラーフラグをオンにする
      attributes.addFlashAttribute("isQueryFormError", true);

      // リダイレクト先にユーザIDを引数として渡す
      attributes.addAttribute("uid", form.getUid());

      // ユーザIDのみの条件で自分自身にリダイレクトする
      return "redirect:/condition";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザIDとニックネームをModelに格納する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        userService
            .getUser(uid)
            .getNickname());

    // 体調を検索し、結果をModelに格納する
    model.addAttribute(
        "logs",
        conditionService
            .query(form)
            .getLogs());

    // HealthQueryFormをModelに追加する(Thymeleaf上ではhealthQueryForm)
    model.addAttribute(new HealthQueryForm());

    // 体調振り返りページ
    return "lookback";
  }

}
