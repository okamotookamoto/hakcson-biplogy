package cs27.educate.howareyou.application.controller.view;

import cs27.educate.howareyou.application.form.HealthConditionForm;
import cs27.educate.howareyou.application.form.HealthQueryForm;
import cs27.educate.howareyou.application.form.UidForm;
import cs27.educate.howareyou.application.form.UserForm;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import cs27.educate.howareyou.domain.entity.Task;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.TaskRepository;
import cs27.educate.howareyou.domain.service.UserService;
import cs27.educate.howareyou.domain.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * ユーザ操作を行うコントローラークラス
 * Thymeleafに渡す値をModelに登録し、ページを表示する
 */
@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  /**
   * ユーザIDを指定して必要な情報を取得し、体調入力ページに入る
   * 未登録のユーザIDが指定された場合、初期ページに移動する
   *
   * @param model
   * @param attributes
   * @param form       ユーザID
   * @return 体調入力ページ
   */
  @GetMapping("/user/enter")
  public String confirmUserExistence(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UidForm form,
      BindingResult bindingResult) {

    // ユーザIDに使用できない文字が含まれていた場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUidValidationError",
          true);

      // 初期画面に戻る
      return "redirect:/";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    String nickname;

    String teamid;

    int status;

    String support;

    int help;

    // ユーザIDからニックネームを取得する
    // ユーザが登録済みかどうかの確認も兼ねている
    try {
      User user = service.getUser(uid);
      nickname = user.getNickname();
      teamid = user.getTeamid();
      status = user.getStatus();
      support = user.getSupport();
      help = user.getHelp();

    } catch (UserValidationException e) { // ユーザIDが未登録であった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }
    // ユーザIDとニックネームをModelに追加する
    attributes.addFlashAttribute(
        "uid",
        uid);
    attributes.addFlashAttribute(
        "nickname",
        nickname);
    attributes.addFlashAttribute(
        "teamid",
        teamid);
    attributes.addFlashAttribute(
        "status",
        status);
    attributes.addFlashAttribute(
        "support",
        support);
    // 体調入力ページ
    return "redirect:/howareyou";
  }

  /**
   * ユーザの情報を取得し、確認画面を表示する
   *
   * @param model
   * @param attributes
   * @param form       ユーザID
   * @return ユーザ情報確認ページ
   */
  @GetMapping("/user/{uid}/information")
  public String searchUserInformation(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UidForm form,
      BindingResult bindingResult, @PathVariable String uid) {

    // ユーザ情報をDBから取得する
    // ユーザが登録済みかどうかの確認も兼ねている
    User user;
    try {
      user = service.getUser(uid);
    } catch (UserValidationException e) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }

    // ユーザ情報をModelに登録する
    model.addAttribute(
        "uid",
        user.getUid());
    model.addAttribute(
        "teamid",
        user.getTeamid());
    model.addAttribute(
        "status",
        user.getStatus());
    model.addAttribute(
        "support",
        user.getSupport());
    model.addAttribute(
        "nickname",
        user.getNickname());
    model.addAttribute(
        "help",
        user.getHelp());

    // ユーザ情報確認ページ
    return "information";
  }

  /**
   * ユーザを登録する
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return 体調記録ページ
   */
  @PostMapping("/user/{uid}/register")
  public String registerUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザを登録する
    try {
      service.createUser(form);
    } catch (UserValidationException e) {
      // ユーザが登録済みであった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザIDとニックネームをModelに登録する
    model.addAttribute(
        "uid",
        form.getUid());
    model.addAttribute(
        "nickname",
        form.getNickname());

    // 体調記録ページ
    return "top";
  }

  /**
   * ユーザ登録が可能か確認する
   * ユーザが登録済みであった場合、ユーザ登録ページに戻る
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return ユーザ登録確認ページ
   */
  @GetMapping("/user/register/confirm")
  public String confirmUserRegistration(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが既に存在するか確認する
    if (service.existsUser(uid)) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // ユーザ登録ページに戻る
      return "redirect:/user/signup";
    }

    // ユーザ情報をModelに追加する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        form.getNickname());
    model.addAttribute(
        "teamid",
        form.getTeamid());

    // ユーザ登録確認ページ
    return "confirmRegistration";
  }

  /**
   * ユーザ情報を更新する
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return 体調記録ページ
   */
  @PostMapping("/user/{uid}/{status}/update")
  public String updateUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm form,
      BindingResult bindingResult, @PathVariable String uid,
      @RequestParam("status") int status) {

    // ユーザ情報を更新する
    try {
      service.updateUser(form);
    } catch (UserValidationException e) {
      // ユーザが存在しない場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }

    System.out.println(uid);

    if (status == 2) {
      String url = "https://api.line.me/v2/bot/message/broadcast";
      String accessToken = "WeTFNz/Oql2o921KGr5uJMwH6kYKsxylH0qFEvbw/xw9QSLZrkK6FRPpisPK1FVG8ulO9Wy7432h+JBsHU8A4smD96xZ6szVdeIkfJw/mI33vR5ZwY3s6oq8DP4GMjFnzs87yoMYNX2xvkeAlcCz+AdB04t89/1O/w1cDnyilFU=";
      // String retryKey = "123e4567-e89b-12d3-a456-426614144005";

      String nickname = service.getUser(uid).getNickname();

      String payload = "{\"messages\": [{\"type\":\"text\", \"text\":\"【SOS】" + nickname + "さんが助けを求めてます!\"}]}";

      try {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        // con.setRequestProperty("X-Line-Retry-Key", retryKey);
        con.setDoOutput(true);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        writer.write(payload);
        writer.flush();
        writer.close();

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // ユーザIDとニックネームをModelに追加する
    model.addAttribute(
        "uid",
        form.getUid());
    model.addAttribute(
        "nickname",
        form.getNickname());

    // 体調記録ページ
    return "top";
  }

  /**
   * ユーザ情報更新が可能か確認する
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return ユーザ情報更新確認ページ
   */
  @GetMapping("/user/{uid}/update/confirm")
  public String confirmUserUpdate(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm form,
      BindingResult bindingResult, @PathVariable String uid) {

    // ユーザが登録済みか確認する
    if (!service.existsUser(uid)) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // リダイレクト先の引数としてユーザIDを渡す
      attributes.addAttribute(
          "uid",
          form.getUid());

      // ユーザ情報取得メソッドにリダイレクトする
      return "redirect:/user/information";
    }

    // ユーザ情報をModelに追加する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        form.getNickname());
    model.addAttribute(
        "teamid",
        form.getTeamid());
    model.addAttribute(
        "status",
        form.getStatus());
    model.addAttribute(
        "support",
        form.getSupport());
    model.addAttribute(
        "help",
        form.getHelp());

    // ユーザ情報更新確認ページ
    return "confirmUpdate";
  }

  /**
   * ユーザを削除する
   *
   * @param model
   * @param attributes
   * @param form       ユーザID
   * @return 初期ページ
   */
  @PostMapping("/user/delete")
  public String deleteUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UidForm form,
      BindingResult bindingResult) {

    // ユーザIDに使用できない文字が含まれていた場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUidValidationError",
          true);

      // 初期画面に戻る
      return "redirect:/";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザを削除する
    service.deleteUser(uid);

    // 初期ページ
    return "redirect:/";
  }

  /**
   * ユーザ削除が可能か確認する
   *
   * @param model
   * @param attributes
   * @param form       ユーザID
   * @return ユーザ削除確認ページ
   */
  @GetMapping("/user/delete/confirm")
  public String confirmUserDelete(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UidForm form,
      BindingResult bindingResult) {

    // ユーザIDに使用できない文字が含まれていた場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUidValidationError",
          true);

      // 初期画面に戻る
      return "redirect:/";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザ情報をDBから取得する
    // ユーザが登録済みかどうかの確認も兼ねている
    User user;
    try {
      user = service.getUser(uid);
    } catch (UserValidationException e) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }

    // ユーザ情報をModelに追加する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        user.getNickname());
    model.addAttribute(
        "email",
        user.getTeamid());

    // ユーザ削除確認ページ
    return "confirmDelete";

  }

  @GetMapping("/{uid}/friend")
  public String searchFriend(
      Model model,
      @PathVariable String uid) {

    // ユーザ情報をDBから取得する
    // ユーザが登録済みかどうかの確認も兼ねている

    String nickname = service.getUser(uid).getNickname();
    String teamid = service.getUser(uid).getTeamid();
    int status = service.getUser(uid).getStatus();
    String support = service.getUser(uid).getSupport();
    int help = service.getUser(uid).getHelp();

    // ユーザ情報をModelに登録する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "teamid",
        teamid);
    model.addAttribute(
        "status",
        status);
    model.addAttribute(
        "support",
        support);
    model.addAttribute(
        "help",
        help);

    model.addAttribute(
        "nickname",
        nickname);

    // 体調を検索し、結果をModelに格納する
    model.addAttribute(
        "logs",
        service
            .query()
            .getLogs());

    // 体調振り返りページ
    return "friendlist";
  }

  @GetMapping("/{uid}/condition")
  public String serachCondition(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated HealthQueryForm form,
      BindingResult bindingResult) {

    // フォームのバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // ユーザIDに使用できない文字が含まれていた場合
      if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
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
