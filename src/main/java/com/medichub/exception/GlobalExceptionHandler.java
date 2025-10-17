package com.medichub.exception;

import com.medichub.dto.auth.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                ex.getMessage()
        );

        log.error("Email already exists: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp(), ex);

        model.addAttribute("error", error);
        return "index";
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailExists(EmailAlreadyExistsException ex, Model model) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                409,
                "Conflict",
                ex.getMessage()
        );

        log.error("Email already exists: status={}, error={}, message={}, timestamp={}",
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

        log.error("Email already exists: status={}, error={}, message={}, timestamp={}",
                error.getStatus(), error.getError(), error.getMessage(), error.getTimestamp(), ex);

        model.addAttribute("error", error);
        return "cart/cart";
    }


    @ExceptionHandler(Exception.class)
    public String handleIllegalArgument(Exception ex, Model model) {

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


    @ExceptionHandler(Exception.class)
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

}
