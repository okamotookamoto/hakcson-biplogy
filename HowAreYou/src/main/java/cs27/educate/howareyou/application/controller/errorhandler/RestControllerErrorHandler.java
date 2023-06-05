package cs27.educate.howareyou.application.controller.errorhandler;

import cs27.educate.howareyou.configuration.exception.Response;
import cs27.educate.howareyou.configuration.exception.ResponseCreator;
import cs27.educate.howareyou.configuration.exception.UserValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;

import static cs27.educate.howareyou.configuration.exception.ErrorCode.*;

@RestControllerAdvice("cs27.educate.howareyou.application.controller.rest")
public class RestControllerErrorHandler {

  /*
   * UserValidationExceptionをBAD_REQUESTとして処理する
   */
  @ExceptionHandler(UserValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> hanleBadRequestException(UserValidationException ex) {

    return ResponseCreator.fail(
        ex.getCode(),
        ex,
        null);
  }

  /*
   * コントローラーのバリデーションに違反したエラーをBAD_REQUESTとして処理する
   */
  @ExceptionHandler({ConstraintViolationException.class,
      MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> handleValidException(Exception ex) {

    return ResponseCreator.fail(
        CONTROLLER_VALIDATION_ERROR,
        ex,
        null);
  }

  /*
   * コントローラーで受け取れない不正なリクエストをBAD_REQUESTとして処理する
   */
  @ExceptionHandler({HttpMessageNotReadableException.class,
      HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> handleConrollerException(Exception ex) {

    return ResponseCreator.fail(
        CONTROLLER_REJECTED,
        ex,
        null);
  }

  /*
   * その他のエラーを重大な未定義エラーとみなしINTERNAL_SERVER_ERRORとして処理する
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Response<Object> handleOtherException(Exception ex) {

    return ResponseCreator.fail(
        OTHER_ERROR,
        ex,
        null);
  }

}
