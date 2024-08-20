package com.snap.wallet.demo.wallet_demo.exception;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.time.LocalTime.now;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleApiException(ApiException ex, HttpServletRequest request) {
        return new ResponseEntity<>(getResponse(request, null, ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(getResponse(request, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("messageCode", ex.getMessageCode());
        errorResponse.put("params", ex.getParams());
        errorResponse.put("timestamp", now().toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("path", request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
