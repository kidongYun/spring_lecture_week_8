# 5주차 강의노트

## Remind

#### Q1. 의존성 관리가 필요한 이유?

- 직접 관리한다면 주입하려고 하는 객체가 가진 의존성도 모두 고려해야 한다.

- 한가지 인스턴스만 존재해도 될 경우 싱글톤 패턴을 직접 구현해야 하며 그렇다면 보일러플레이트 코드들이 많아진다.

#### Q2. 의존성 주입을 할 때 사용하는 스프링 어노테이션은 ?

- @Autowired

#### Q3. @Autowired 어노테이션이 필드에 존재하냐, 생성자에 존재하냐에 따라 필드주입, 생성자주입으로 갈린다. 스프링은 기본적으로 생성자 주입을 선호하는데 그 이유는?

- 순환참조 이슈 때문에.

#### Q4. 빌더 패턴이 가지는 이점?

- 명시적으로 필드에 값을 주입할 수 있다.
- Immutable 하게 코드 작성이 가능하다.
- 필드가 많을 경우 생성자 오버로딩을 많이 하지 않고도 주입이 가능하다.

#### Q5. ResponseEntity 객체를 활용하면 얻을 수 있는 이점.

- Http Head 영역에 존재하는 Http Status 값을 직접 컨트롤 할 수 있다.

#### Q6. Restful API 제공하는 기본적인 Http Method 4가지

- GET : 조회
- POST : 생성
- PUT : 수정
- DELETE : 제거

#### Q7. DTO 객체를 생성하는 이유?

- Domain Layer 의 객체와 Presentation Layer 의 객체를 구분하기 위함.
- 두 계층 간 객체들의 역할은 분명히 다르다.

#### Q8. Transaction 이란?

- ACID
  - Atomic (원자성) : 업무가 하나의 일련 과정이어야 한다
  - Consistency (일관성) : 업무 결과가 작업 끝난 후에도 유지되어야 한다.
  - Isolation (고립성) : 업무 처리 시 다른 업무가 끼어들 수 없어야 한다.
  - Durability (영구성) : 작업 내용이 영구히 저장되어야 한다. (이건 데이터베이스 기준이기 때문에 어플리케이션 레벨에서는 달라질 수 있다.)

#### Q9. 정적 팩토리 메서드 (of Pattern)

- 객체 생성하는 코드를 팩토리 메서드 형태로 제공
- 코드 중복 최소화
- 비즈니스와 객체 생성에 대한 영역을 구분할 수 있음.

#### Q10. 컨트롤러 앞단에서 예외처리 해주도록 스프링에서 제공해주는 기능 ?

- @ControllerAdvice, @RestControllerAdvice

#### Q11. 자바 8 이후 null 처리를 강제하기 위해서 추가된 기능 ?

- Optional

#### Q12. 테스트코드를 작성하는 이유

- 테스트 코드는 다른 개발자들에게 하나의 문서로 사용될 수 있다.
  - 내가 만든 함수에 대한 테스트를 명시적으로 보여줌으로써 다른 개발자들은 이 테스트 코드를 보고 내가 만든 함수가 어떻게 동작하는지를 보다 정확히 알 수 있다.
  - 작성된 함수가 동작할 때 흔하지 않은 예외 케이스들을 접할 수 있는데 그럴때 마다 테스트 케이스를 추가해두면 이런 부분에 대해서도 추후 다른 개발자들에게 인계가 강제적으로 가능하다.

- 테스트 코드가 실패했을 시 빌드 실패가 되도록 강제할 수 있어서 후 리팩토링이나 비지니스가 수정되어 소스를 수정해야 할 때 방어책이 될 수 있다.

## Controller 유닛 테스트

- @WebMvcTest 어노테이션은 Controller 와 관련된 객체만 스프링 컨테이너 빈으로 등록해준다.

```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

}
```

- Mock 객체를 스프링 컨테이너에 등록하고 싶다면 @MockBean 어노테이션을 활용

```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    ArticleService articleService;
}
```

- 컨트롤러 함수들은 api 진입점으로 활용되기 때문에 가짜 api client(postman 같은) 역할을 하는 MockMvc 를 추가

```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    ArticleService articleService;
    @Autowired
    MockMvc mvc;
}
```

- MockMvc 를 활용해서 테스트 코드 구현

```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    ArticleService articleService;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Response.ok() 함수 호출시 code 값이 ApiCode.SUCCESS 값을 가져야 한다.")
    public void get_whenYouCallOk_ThenReturnSuccessCode() throws Exception {
        // given
        Long idStub = 1L;
        when(articleService.findById(idStub)).thenReturn(Article.builder().id(idStub).build());

        // when, then
        mvc.perform(get("/api/v1/article/" + idStub)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}
```

- Result 까지 비교해야 한다면 아래처럼 구현 가능

```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    ArticleService articleService;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Response.ok() 함수 호출시 code 값이 ApiCode.SUCCESS 값을 가져야 한다.")
    public void get_whenYouCallOk_ThenReturnSuccessCode() throws Exception {
        // given
        Long idStub = 1L;
        when(articleService.findById(idStub)).thenReturn(Article.builder().id(idStub).build());

        // when
        String response = mvc.perform(get("/api/v1/article/" + idStub)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(ApiCode.SUCCESS.getName()).isSubstringOf(response);
    }
}
```

## 통합 테스트

```java
@SpringBootTest
public class ArticleControllerIntegrationTest {

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

    mvc.perform(post("/api/v1/article/")
                    .characterEncoding("utf-8")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestBodyStub))
            )
            .andDo(print())
            .andExpect(status().isOk());

    // when
    long size = articleRepository.count();

    // then
    assertThat(size).isEqualTo(1);
  }
}
```

## Validation

#### 직접 Validation 처리

- API 앞단에서 미리 요청 데이터를 검증하고 후에 시스템에 이로 인한 문제가 없도록 한다.
- 이해를 위해 Article 객체의 title, content 필드는 필수값이라고 하자.

- Asserts 유틸 클래스를 활용해 Controller 에서 Validation 처리를 해보자.

```java
public enum ApiCode {
    /* ... */
    BAD_REQUEST("CM0002", "요청 정보가 올바르지 않습니다") /* 새로운 Code 추가 */
    ;
    
    /* ... */
}
```

입력 문자열이 null 이거나 __빈 공백__ 일 수 있기 때문에 이를 검증하기 위한 _isBlank_ 함수를 만든다.

```java
public class Asserts {
    
    /* ... */
  
    public static void isBlank(@Nullable String str, ApiCode code, String msg) {
        if(Strings.isBlank(str)) {
            throw new ApiException(code, msg);
        }
    }
}
```

```java
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    
   /* ... */ 
    
    @PostMapping
    public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
      Asserts.isBlank(request.getTitle(), ApiCode.BAD_REQUEST, "'title' 은 필수 값입니다.");
      Asserts.isBlank(request.getContent(), ApiCode.BAD_REQUEST, "'content' 는 필수 값입니다.");
      
      return Response.ok(articleService.save(Article.of(request)));
    }
    
    /* ... */
}
```

- 클라이언트에서 API 요청 해보면서 정상적으로 요청 데이터 검증이 되었는지 확인해보자.

```http request
### POST ARTICLE
POST http://localhost:8080/api/v1/article
Content-Type: application/json

{
  "title" : "아티니어 스프링 강좌",
  "content" : "아티니어 스프링 강좌 많관부!!!"
}
```

#### Spring Validation 으로 Validation 처리

- 데이터를 검증하는 케이스는 일반적으로 _Null, 빈값, 날짜의 포맷, 금액의 포맷, 요구되어지지 않은 값_ 어느정도 정형화 되어있다.
- 스프링에서는 _spring-validation_ 이라는 의존성으로 보다 심플하게 validation 처리 할 수 있는 기능을 제공한다.

- 관련 의존성을 추가하자.

```groovy
// build.gradle

dependencies {
  // ...
  
  implementation 'org.springframework.boot:spring-boot-starter-validation'
  
  // ...
}
```

- Spring Validation 을 적용하기 위해서는 @Valid 어노테이션을 추가해야 한다.

```java
public class ArticleController {
    
    // ...
  
    @PostMapping
    public Response<Long> post(@RequestBody @Valid ArticleDto.ReqPost request) {
        return Response.ok(articleService.save(Article.of(request)));
    }
    
    // ...
}
```

```java
public class ArticleDto {
    @Getter
    @Builder
    public static class ReqPost {
        @NotBlank
        String title;
        @NotBlank
        String content;
    }
    
    // ...
}
```

- title, content 에 공백 값을 넣고 테스트해보면 예외가 발생함을 알 수 있다.
- Validation 처리가 가지는 중요한 요소 중 또 하나는 API 를 사용하는 클라이언트에게 어떤 값이 잘못되어 있는지 정보를 제공할 수 있다는 점이다.
- 우리가 직접 작성한 Validation 코드의 경우 API 로 문제점을 전달해주고 있지만. Spring Validation 은 서버 로그에만 오류가 기록되고 Client 에는 400 에러만 내려간다.
- Spring Validation 의 오류 내용이 API 로 적절하게 내려가도록 해주자.


```java
@RestControllerAdvice
public class ControllerExceptionHandler {
    
    // ...
  
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Response.<String>builder().code(ApiCode.BAD_REQUEST).data(e.getMessage()).build();
    }
}
```

- data 부분이 우리가 직접 만든 것처럼 내용이 깔끔하지는 않지만. 필요한 정보는 모두 담고 있다.
- 원한다면 해당 문구를 파싱해서 보다 깔끔하게 내려줄수도 있다.

#### Optional) @RequestParam, @RequestBody

- GET 통신에서 QueryString 를 통해 받는 데이터를 Spring 은 @RequestParam 으로 받을 수 있다.
- POST 통신에서 Body 를 통해 받는 데이터를 Spring 은 @RequestBody 으로 받을 수 있다.

- @RequestBody 기반으로 들어온 데이터가 검증 실패할 경우 MethodArgumentNotValidException 오류를 던지지만 @RequestParam 은 ConstraintViolationException 오류를 던진다.
- 이 예제에서는 @RequestParam 을 사용하지 않기 때문에 필요하지는 않지만 그래도 이를 위한 예외 처리도 적용해보도록 하자.

```java
@RestControllerAdvice
public class ControllerExceptionHandler {
    // ...
  
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
```

## Logging

- API 팀의 실무에서 가장 흔하게 로깅 작업을 하는 부분은 API 의 최앞단인 요청부분과 최끝단인 응답부분을 로깅하는 것.

#### 1. 가장 심플한 로깅.

```http request
### GET ARTICLE
GET http://localhost:8080/api/v1/article/1

### POST ARTICLE
POST http://localhost:8080/api/v1/article
Content-Type: application/json

{
  "title" : "title",
  "content" : "content"
}
```

```java
public class ArticleService {
    // ...

  public Article findById(Long id) {
    log.info("Request : id - " + id);

    Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));

    log.info("Response : article - " + article.toString());

    return article;
  }
  
  // ...
}
```

```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Article {
    // ...
}
```

#### 2. Proxy Pattern 을 활용한 로깅

- 현재 로깅 방법은 비즈니스 영역과 로깅에 대한 영역이 공존.
- 사실 두 기능은 분리될 수 있음. (서로 의존적인지를 판단해보라)
- 분리가능한 기능을 한 곳에 두는 것은 두 기능의 의존성을 높이고, 결합도를 낮추는 행위.

```java
public interface ArticleServiceable {
    Long save(Article request);

    Article findById(Long id);

    Article update(Article request);

    void delete(Long id);
}
```

```java
@RequiredArgsConstructor
@Service
public class ArticleService implements ArticleServiceable {
  private final ArticleRepository articleRepository;

  @Override
  public Long save(Article request) {
      // ...
  }

  @Override
  public Article findById(Long id) {
      // ...
  }

  @Override
  @Transactional
  public Article update(Article request) {
     // ... 
  }

  @Override
  public void delete(Long id) {
      // ...
  }
}
```