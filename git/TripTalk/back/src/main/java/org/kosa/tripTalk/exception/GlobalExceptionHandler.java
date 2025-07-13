package org.kosa.tripTalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ JwtException.class, IllegalArgumentException.class })
  public ResponseEntity<ErrorResponse> handleJwtException(Exception ex) {
      // 로그 출력
      System.out.println("JWT 예외 발생: " + ex.getMessage());

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new ErrorResponse("TOKEN_EXPIRED", "Access token expired"));
  }
	
}
