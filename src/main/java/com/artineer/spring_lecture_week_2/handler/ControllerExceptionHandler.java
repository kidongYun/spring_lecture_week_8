package com.artineer.spring_lecture_week_2.handler;

import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Response.<String>builder().code(ApiCode.BAD_REQUEST).data(e.getMessage()).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> contraintViolationException(ConstraintViolationException e) {
        return Response.<String>builder().code(ApiCode.BAD_REQUEST).data(e.getMessage()).build();
    }
}
