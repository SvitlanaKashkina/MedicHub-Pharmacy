package com.medichub.exception;

import com.medichub.dto.auth.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                409,
                "Conflict",
                ex.getMessage()
        );

        log.error("EmailAlreadyExistsException: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp(), ex);

        model.addAttribute("error", error);
        return "error/errorPage";
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage()
        );
        log.error("Resource not found: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp());

        model.addAttribute("error", error);
        return "cart/cart";
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFound(UsernameNotFoundException ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                400,
                "Bad Request",
                ex.getMessage()
        );

        log.error("UsernameNotFoundException: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp());

        model.addAttribute("error", error);

        return "signup";
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                400,
                "Bad Request",
                ex.getMessage()
        );

        log.error("IllegalArgumentException: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp());

        model.addAttribute("error", error);

        return "signup";
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.error("Validation failed: {} invalid fields. Details: {}", errors.size(), errors);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                ex.getMessage()
        );
        log.error("RuntimeException: status={}, error={}, message={}, timestamp={}",error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp(), ex);
        model.addAttribute("error", "Error with the order: " + ex.getMessage());
        return "checkout/checkout";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                ex.getMessage()
        );

        log.error("AllException: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp(), ex);

        model.addAttribute("error", error);
        return "index";
    }

}
