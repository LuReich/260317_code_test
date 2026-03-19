package backend.common;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException e) {
        FieldError first = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String msg = (first instanceof FieldError fe)
                ? (fe.getField() + " " + fe.getDefaultMessage())
                : "validation error";
        return ResponseEntity.badRequest().body(ApiResponse.error(400, msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(IllegalArgumentException e) {
        String code = e.getMessage();
        if ("UNAUTHORIZED".equals(code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }
        if ("FORBIDDEN".equals(code)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "권한이 없습니다."));
        }
        if ("INVALID_CREDENTIALS".equals(code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        if ("NO_FIELDS_TO_UPDATE".equals(code)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "수정할 값이 없습니다."));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "로그인이 필요합니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "권한이 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleEtc(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "서버 오류"));
    }
}
