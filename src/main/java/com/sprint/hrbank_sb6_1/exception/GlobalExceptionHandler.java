package com.sprint.hrbank_sb6_1.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 400 잘못된 요청
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<Map<String, Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    body.put("details",ex.getBindingResult().getFieldErrors()
        .stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .toList());

    return ResponseEntity.badRequest().body(body);
  }

  // 400 잘못된 타입 (예: UUID 형식 오류)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    body.put("details", ex.getName() + " should be of type " + ex.getRequiredType());
    return ResponseEntity.badRequest().body(body);
  }

  // 404 리소스를 찾을 수 없음
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.");
    body.put("details", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  // 405 지원하지 않는 메소드
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청입니다.");
    body.put("details", ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
  }

  // 409 중복 등 충돌 오류
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.CONFLICT, "중복된 데이터 입니다.");
    body.put("details", ex.getMostSpecificCause().getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.NOT_FOUND,"지원하지 않는 API 경로 입니다.");
    body.put("details", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  // 500 서버 내부 오류 (예상치 못한 모든 오류 처리)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    Map<String, Object> body = setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 입니다.");
    body.put("details", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private Map<String, Object> setErrorMessage(HttpStatus status, String message) {
      Map<String, Object> errors = new LinkedHashMap<>();
      errors.put("timestamp" , Instant.now().toString());
      errors.put("status" , status.value());
      errors.put("message", message);
      return errors;

    }
}
