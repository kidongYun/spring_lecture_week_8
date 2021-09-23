package com.artineer.spring_lecture_week_2.dto;

import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {
    private ApiCode code;
    private T data;
}
