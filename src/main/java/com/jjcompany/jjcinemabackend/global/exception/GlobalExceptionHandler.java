package com.jjcompany.jjcinemabackend.global.exception;

import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/*프로젝트 전체 컨트롤러에 자동으로 적용돼요.
각 컨트롤러마다 try-catch 넣을 필요 없이, 이 파일 하나만 만들어두면
어디서 어떤 컨트롤러/서비스든 IllegalStateException이 터지면
자동으로 이 핸들러가 낚아채서 404 + ApiResponse.fail(메시지) 형식으로 바꿔줘요.*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    //(리소스를)못 찾음 -> 404
    //"지금 이 객체/시스템 상태에서는 이 동작을 할 수 없다" 상황. 추후 NotFoundException으로 분리 가능.
    //순수 자바 표준 예외. HTTP나 상태코드 개념이 전혀 없고, "지금 이 상태에서 이 동작은 안된다"는 뜻만 담고 있음.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(IllegalStateException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    //잘못된 (요청)값 -> 400
    //메서드에 잘못된 값(인자)이 넘어왔을 때 쓰는 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException e)
    {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    //그 외 예상 못한 에러 -> 500(스택 트레이스는 안보여주고 메시지만)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e)
    {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버 오류가 발생했습니다."+e.getMessage()));
    }

    //스프링이 만든 전용 예외. 던지는 그 순간에 상태코드를 직접 지정할 수 있다. -> 409
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatus(ResponseStatusException e){
        return ResponseEntity.status(e.getStatusCode())
                .body(ApiResponse.fail(e.getReason()));
        //e.getReason() → "장르를 찾을 수 없습니다"
        //e.getMessage() → "404 NOT_FOUND \"장르를 찾을 수 없습니다\""
        //참고)IllegalStateException엔 getReason()이라는 메서드 자체가 없다.
    }
}
