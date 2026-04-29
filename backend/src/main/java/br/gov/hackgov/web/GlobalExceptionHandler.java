package br.gov.hackgov.web;

import br.gov.hackgov.exception.BusinessException;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError error = new ApiError(OffsetDateTime.now(), 404, "NOT_FOUND", ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        ApiError error = new ApiError(OffsetDateTime.now(), 400, "BUSINESS_RULE", ex.getMessage(), List.of());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex) {
        ApiError error = new ApiError(OffsetDateTime.now(), 403, "FORBIDDEN", "Acesso negado", List.of());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(err -> {
                    if (err instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .toList();

        ApiError error = new ApiError(OffsetDateTime.now(), 400, "VALIDATION_ERROR", "Dados inválidos", details);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError error = new ApiError(OffsetDateTime.now(), 500, "INTERNAL_ERROR", "Erro interno", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
