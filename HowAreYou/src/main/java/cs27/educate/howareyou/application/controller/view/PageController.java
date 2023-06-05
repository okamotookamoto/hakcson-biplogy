package cs27.educate.howareyou.application.controller.view;

import cs27.educate.howareyou.application.form.HealthConditionForm;
import cs27.educate.howareyou.application.form.UserForm;
import cs27.educate.howareyou.domain.entity.User;
import cs27.educate.howareyou.domain.repository.UserRepository;
import cs27.educate.howareyou.domain.service.UserService;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

  // private final UserService service;
  private final UserService service;
  private final UserRepository users;

  @GetMapping("/")
  public String showLandingPage() {
    return "index";
  }

  @GetMapping("/user/signup")
  public String showUserRegistrationPage(Model model) {

    // UserFormをModelに追加する(Thymeleaf上ではuserForm)
    model.addAttribute(new UserForm());

    return "register";
  }

  @GetMapping("/howareyou")
  public String showConditionInputPage(Model model) {

    return "top";
  }

  @GetMapping("/task")
  public String showTaskInputPage(Model model) {

    // HealthConditionFormをModelに追加する(Thymeleaf上ではhealthConditionForm)
    model.addAttribute(new HealthConditionForm());

    return "input";
  }

  @PostMapping("/{uid}/{log.fnickname}/broadcast")
  public String broadcast(
      @PathVariable String uid,
      @PathVariable("log.fnickname") String fnickname,
      @RequestParam("nickname") String nickname,
      @RequestParam("teamid") String teamid,
      @RequestParam("status") int status,
      @RequestParam("support") String support,
      @RequestParam("help") int help) {

    // DB上のユーザ情報を更新し、新しいユーザ情報を戻り値として返す
    users.save(new User(
        uid,
        nickname,
        teamid,
        status,
        support,
        help + 1));

    String url = "https://api.line.me/v2/bot/message/broadcast";
    String accessToken = "WeTFNz/Oql2o921KGr5uJMwH6kYKsxylH0qFEvbw/xw9QSLZrkK6FRPpisPK1FVG8ulO9Wy7432h+JBsHU8A4smD96xZ6szVdeIkfJw/mI33vR5ZwY3s6oq8DP4GMjFnzs87yoMYNX2xvkeAlcCz+AdB04t89/1O/w1cDnyilFU=";
    // String retryKey = "123e4567-e89b-12d3-a456-426614144005";
    // String payload = "{\"messages\": [{\"type\":\"text\", \"text\":\"" + uid
    // + "\"}, {\"type\":\"text\", \"text\":\"Hello, " + fnickname + "\"}]}";

    System.out.println(nickname);
    System.out.println(fnickname);

    String payload = "{\"messages\": [{\"type\":\"text\", \"text\":\"【GREAT】" + nickname + "さんが" + fnickname
        + "さんを助けました！！\"}]}";

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
    return "redirect:/"; // フォームが表示されるindex.htmlにリダイレクト
  }

  @GetMapping("/{uid}/meeting")
  public String searchFriend(
      Model model,
      @PathVariable String uid) {

    // 体調を検索し、結果をModelに格納する

    model.addAttribute(
        "logs",
        service
            .mquery()
            .getLogs());

    // 体調振り返りページ
    return "ranking";
  }

}
