package com.artineer.spring_lecture_week_2.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Article {
    Long id;
    String title;
    String content;
}
