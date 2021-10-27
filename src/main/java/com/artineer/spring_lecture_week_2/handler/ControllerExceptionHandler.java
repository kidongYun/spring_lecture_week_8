package com.artineer.spring_lecture_week_2.handler;

import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }


    /* @RequestBody 데이터 검증 실패시 발생한다. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Response.<String>builder().code(ApiCode.BAD_REQUEST).data(e.getMessage()).build();
    }

    /* @RequestParam 데이터 검증 실패시 발생한다. */
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> constraintViolationException(ConstraintViolationException e) {
        return Response.<String>builder().code(ApiCode.BAD_REQUEST).data(e.getMessage()).build();
    }
}
