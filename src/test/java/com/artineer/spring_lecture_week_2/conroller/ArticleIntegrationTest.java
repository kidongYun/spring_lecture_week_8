package com.artineer.spring_lecture_week_2.conroller;

import com.artineer.spring_lecture_week_2.domain.ArticleRepository;
import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ArticleIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ArticleRepository articleRepository;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("post() 실행되면 article 객체가 새로 생성되어야 한다")
    public void post_whenItIsOccured_thenArticleShouldbeStored() throws Exception {
        // given
        ArticleDto.ReqPost requestBodyStub = ArticleDto.ReqPost.builder()
                .title("title")
                .content("content")
                .build();

        // when
        mvc.perform(post("/api/v1/article")
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBodyStub))
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        long size = articleRepository.count();
        assertThat(size).isEqualTo(1);
    }
}
