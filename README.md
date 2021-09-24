# 2주차 강의 노트

## Spring 2주차 프로젝트 생성
- https://start.spring.io/

- Project : Gradle Project
- Language : Java
- Spring Boot : 2.5.4
- Project Metadata
    - Group : com.artineer
    - Artifact : spring_lecture_week_2
    - Name : spring_lecture_week_2
    - Package name : com.artineer.spring_lecture_week_2
    - Packaging : Jar
    - Java : 11

- 웹 의존성 추가
```groovy
	implementation 'org.springframework.boot:spring-boot-starter-web'
```

## Spring Web 진입점 만들기
- Controller 객체 생성

- 간단한 ping/pong api 구현

```java
@RestController
public class PingController {
  @GetMapping("/")
  public String ping() {
    return "pong";
  }
}
```

- 크롬 브라우저로 접속해서 테스트

- @GetMapping("/") 일 경우 에는 뒤 path 정보 생략이 가능하다
```java
@RestController
public class PingController {
  @GetMapping
  public String ping() {
    return "pong";
  }
}
```

## Postman 소개
- Ping/Pong API 정상 동작하는지 확인
- https://www.postman.com/

## .http 파일 소개
- Intellij IDE 제공해주는 HTTP 통신 클라이언트
- 코드 기반이기 때문에 형상 관리가 가능하다
- Ultimate 버전에서만 제공

--------------------------------------

## API 구조 구현해보기

- API 에는 기본적으로 응답코드와, 응답설명이 필요
    - 응답코드는 운영되고 있는 서비스에서 오류가 발생했을 때 어떤 이슈인지 바로 확인하기 위함.
    - API 응답이 정상적으로 내려간 것인지 혹은 아니라면 왜 오류가 났는지 등에 대한 설명을 위해 필요.

```java
@RestController
public class PingController {
    @GetMapping
    public Object ping() {
        return Map.of(
                "code", "0000",
                "desc", "정상입니다",
                "data", "pong"
        );
    }
}
```

```json
{
    "code": "0000",
    "data": "pong",
    "desc": "정상입니다"
}
```

- 현재 Map 구조로 응답객체를 anonymous 하게 내려주는데 이 보다는 응답객체라고 명시해주는 것이 좋다.

```java
public class Response {
  private String code;
  private String desc;
  private String data;

  public Response(String code, String desc, String data) {
    this.code = code;
    this.desc = desc;
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
```

```java
@RestController
public class PingController {
    @GetMapping
    public Object ping() {
        return new Response("0000", "정상입니다", "pong");
    }
}
```

- API 응답구조를 만드는 코드가 항상 중복되기 때문에 이를 개선해보자
- API code, desc 이 둘간의 결합도는 높아야 하지만 이 구조에서는 낮기 때문에 변경이 일어나면 다시 모두 맞추어야 한다. 이를 개선해보자

```java
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiCode {
  /* COMMON */
  SUCCESS("CM0000", "정상입니다")
  ;

  private final String name;
  private final String desc;

  ApiCode(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }
}
```

```java
public class Response {
    private ApiCode code;
    private String data;

    public Response(ApiCode code, String data) {
        this.code = code;
        this.data = data;
    }

    public ApiCode getCode() {
        return code;
    }

    public void setCode(ApiCode code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
```

```json
{
  "code": {
    "name": "CM0000",
    "desc": "정상입니다"
  },
  "data": "pong"
}
```

--------------------------------------------

## 빌더패턴 소개

```java
class Obj {
    Stirng param1;
    Stirng param2;
    Stirng param3;
    Stirng param4;
    Stirng param5;
    Stirng param6;
    
    public Example(String param1, String param2, String param3, String param4, String param5, String param6) {
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
    }
}
```

```java
class Process {
  public static void main(String[] args) {
      final Obj obj = new Obj("p1", "p2", "p3", "p4", "p5", "p6");
      Obj obj = new Obj("p1");
    
    // 10000 줄의 코드
    obj.setParam2("changed p2");
    
    return obj;
  }
}
```

- 3개의 의존성이 있을 때 생성자만을 활용한다면 총 6개의 생성자가 필요.
    - 파라미터를 구분할 수 있는게 순서 뿐이기 때문에 어떤 파라미터인지 읽기 어렵다. 
  
- 그래서 SETTER 를 사용함 하지만 이는 함수형 프로그래밍 패러다임에서 좋지 않는 코드로 인지됨.
  - 변경 가능성이 있는 구조는 읽기 어렵게 만든다.
  
- 변경 가능하지 않는 객체를 만드는 것이 훨씬 가독성에 좋고, 오류 찾기가 쉬워진다.

- 의존성이 많다면 빌더패턴을 사용해보자

- 생성자에 비해 빌더는 어떤 파라미터를 입력하는지 정확히 눈에 보임으로 더 가독성이 좋다.

```java
public class Response {
    private ApiCode code;
    private String data;

    private Response() { }

    public ApiCode getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Response response;

        public Builder() {
            this.response = new Response();
        }

        public Builder code(ApiCode code) {
            this.response.code = code;
            return this;
        }

        public Builder data(String data) {
            this.response.data = data;
            return this;
        }

        public Response build() {
            return this.response;
        }
    }
}
```

# lombok 소개

- 좋은 구조를 코드를 작성하려다보니 점점 보일러플레이트 코드들이 많아지고 있다.

- lombok 을 활용하면 이러한 코드들을 개선할 수 있다.

```groovy
dependencies {
  // ...
	compileOnly 'org.projectlombok:lombok:1.18.20'
	annotationProcessor 'org.projectlombok:lombok:1.18.20'
  // ...
}
```

```java
@Getter
@Builder
public class Response {
    private final ApiCode code;
    private final String data;
}
```

```java
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiCode {
    /* COMMON */
    SUCCESS("CM0000", "정상입니다")
    ;

    private final String name;
    private final String desc;

    ApiCode(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
```

-------------------------------------------------------------

## 동적 타입의 활용

- Response 객체의 구조를 보면 data 는 항상 String 타입을 받는다. 즉 다른 타입의 데이터들은 내릴 수 없으며 항상 String 구조를 가져야 한다.

- 제네릭 문법을 활용하면 이 이슈를 개선할 수 있다.

```java
@Getter
@Builder
public class Response<T> {
    private final ApiCode code;
    private final T data;
}
```

```java
@RestController
public class PingController {
    @GetMapping
    public Response<String> ping() {
        return Response.<String>builder()
                .code(ApiCode.SUCCESS)
                .data("pong")
                .build();
    }
}
```

-------------------------------------------------------------

## ResponseEntity 객체 활용해보기

- 지금까지의 API 들은 HttpStatus Code 직접 관리할 수 없었으며 200(OK) 응답이 내려와야만 위의 API 결과물 들을 얻을 수 있다.

- Http Status Code 직접 관리해야 한다면 ResponseEntity 객체를 활용할 수 있다.

```java
@RestController
public class PingController {
  @GetMapping
  public ResponseEntity<Response<String>> ping() {
    Response<String> response = Response.<String>builder()
            .code(ApiCode.SUCCESS).data("pong").build();

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
  }
} 
```

-------------------------------------------------------------

## REST API

- HTTP METHOD 로 행위를 표현
    - GET : 조회
    - POST : 생성
    - PUT : 변경
    - DELETE : 제거

- HTTP URI 로 리소스를 표현
    - /users/13/posts/12 ex) 13번 유저의 12번 글

- HTTP STATUS CODE, hateoas ...

- REST 구조가 가지는 장점
    - 클라를 위한 API 가 아닌 비지니스 도메인을 표현하는 API
    - Resource / domain 중심으로 표현하는 API

## Article 객체 생성
```java
@Getter
@Builder
public class Article {
  Long id;
  String title;
  String content;
}
```

## ArticleService 생성 및 save 함수 구현
- Service 는 실제 비지니스 로직을 처리하기 위한 영역
- 동일한 Service 기능을 다양한 controller 에서 접근이 가능하도록 구분

```java
@Service
public class ArticleService {
  private Long id = 0L;
  final List<Article> database = new ArrayList<>();

  public Long save(Article request) {
    Article domain = Article.builder()
            .id(getId())
            .title(request.getTitle())
            .content(request.getContent())
            .build();

    database.add(domain);
    return domain.getId();
  }

  private Long getId() {
    return ++id;
  }
}
```

## ArticleController 생성 및 ArticleService 생성자 주입

- ArticleService  생성자 주입
```java
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
}
```

- ArticleService 생성자 주입 lombok 을 활용해서 개선
```java
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;
}
```

# Article DTO  (Data Transfer Object) 객체 생성
- Domain 영역의 객체와 Presentation 영역의 객체가 구분되어야 한다.
- ex) 기획자가 글 번호를 원화처럼 천,만,억 단위를 구분해달라고 한다 10000 -> 10,000

```java
public class ArticleDto {
  @Getter
  public static class ReqPost {
    String title;
    String content;
  }
}
```

## ArticleController Post 구현
```java
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Long id = articleService.save(article);

        return Response.<Long>builder().code(ApiCode.SUCCESS).data(id).build();
    }
}
```

## ArticleService findById 함수 구현

```java
public class ArticleService {
    //...
  
  public Article findById(Long id) {
    return database.stream().filter(article -> article.getId().equals(id)).findFirst().get();
  }   
  
    // ...
}
```

## ArticleDto 에 Res 객체 추가

```java
//...

    @Builder
    public static class Res {
        private String id;
        private String title;
        private String content;
    }
    
    //...
```

## ArticleController Get 구현

```java
public class ArticleController {
    // ...
  @GetMapping("/{id}")
  public Response<ArticleDto.Res> get(@PathVariable Long id) {
    Article article = articleService.findById(id);

    ArticleDto.Res response = ArticleDto.Res.builder()
            .id(String.valueOf(article.getId()))
            .title(article.getTitle())
            .content(article.getContent())
            .build();

    return Response.<ArticleDto.Res>builder().code(ApiCode.SUCCESS).data(response).build();
  }   
  // ...
}
```
---------------------------------

## 2주차 강의 자료

https://www.youtube.com/watch?v=ZL3Ttc_b8wI