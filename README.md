# 6주차 강의노트

## Logging

- 운영되고 있는 시스템에서 로깅을 하는 행위는 과거에 통신한 데이터들의 흐름을 쫓을 수 있는 유일한 수단이기 때문에 굉장히 중요하다
- 무분별한 로깅은 정말 필요한 정보를 보기 어렵게 만들기 때문에 필요한 것만 로깅을 하자
- 로깅 체계를 정확히 만들어서 로깅 정보를 통해 오류의 원인을 바로 파악할 수 있도록 하는 것이 중요하다.

#### 1. 가장 심플한 로깅.

- 가장 많이 사용하게 될 조회 (GET) API 를 통해 간단하게 로깅하는 방법을 익혀보자.

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleService {
    
    // ...

  public Article findById(Long id) {
    log.info("Request : id - {}", id);

    Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));

    log.info("Response : article.title - {}, article.content - {}", article.getTitle(), article.getContent());

    return article;
  }
  
  // ...
}

```

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

- 후에 WAS 서버 로그 파일에서 로그 기록들을 확인이 가능하다.

#### 2. Proxy Pattern 을 활용한 로깅

- 위 로깅 방법은 비즈니스 영역과 로깅에 대한 영역이 공존.
- 사실 두 기능은 분리될 수 있음.
- 응집도를 더 높이고, 결합도를 낮출수 있다.
- 분리가능한 기능을 한 곳에 두는 것은 두 기능의 의존성을 높이고, 결합도를 낮추는 행위.

- 우선 ArticleService -> ArticleServiceImpl 로 이름 변경

```java
public class ArticleServiceImpl {
    // ...
}
```

- ArticleService 인터페이스 생성

```java
public interface ArticleService {
  Long save(Article request);

  Article findById(Long id);

  Article update(Article request);

  void delete(Long id);
}
```

- ArticleServiceImpl 은 ArticleService 인터페이스를 구현

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
  private final ArticleRepository articleRepository;

  @Override
  public Long save(Article request) {
    return articleRepository.save(request).getId();
  }

  @Override
  public Article findById(Long id) {
    return articleRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));
  }

  @Transactional
  @Override
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    article.update(request.getTitle(), request.getContent());

    return article;
  }

  @Override
  public void delete(Long id) {
    articleRepository.deleteById(id);
  }
}
```

- ArticleService 객체를 위한 프록시 객체를 생성하자
- 프록시 객체는 원본 구현체를 감싸고 있으며 구현체를 실행시키는 역할을 한다.

```java
@RequiredArgsConstructor
@Service
public class ArticleServiceProxy implements ArticleService {
    private final ArticleService articleService;

    @Override
    public Long save(Article request) {
        return articleService.save(request);
    }

    @Override
    public Article findById(Long id) {
        return articleService.findById(id);
    }

    @Override
    public Article update(Article request) {
        return articleService.update(request);
    }

    @Override
    public void delete(Long id) {
        articleService.delete(id);
    }
}

```

- 구현체를 실행시키기 전, 실행시키고 난 후에 원하는 작업을 넣을 수가 있다.
- 여기서는 로깅하는 작업을 추가했다.

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceProxy implements ArticleService {
  private final ArticleService articleService;

  @Override
  public Long save(Article request) {
    return articleService.save(request);
  }

  @Override
  public Article findById(Long id) {
    // Pre-Process
    log.info("Request : id - {}", id);

    Article article = articleService.findById(id);

    // Post-Process
    log.info("Response : article.title - {}, article.content - {}", article.getTitle(), article.getContent());

    return article;
  }

  @Override
  public Article update(Article request) {
    return articleService.update(request);
  }

  @Override
  public void delete(Long id) {
    articleService.delete(id);
  }
}
```

- 여기서 중요한 한가지는 클라이언트는 프록시 라는 객체를 사용하는 것을 모르며 단순히 ArticleService 라는 인터페이스만 안다는 점이다.
- 인터페이스는 클라-서버 간 주고 받을 메시지(규격) 을 정해주며, 서로 간의 결합도를 느슨하게 해준다.
- 즉 클라는 서버의 상세 구현 내용을 알 필요가 없고, 반대로 서버는 인터페이스 규격만 맞추어준다면 원하는 대로 개발을 진행할 수 있다.

```
ArticleServiceImpl 클릭해서 사용하고 있는 부분들을 모두 ArticleService 로 변경해주자.
```

- 실행해보면 아래와 같은 오류가 난다.

```
Parameter 0 of constructor in ...ArticleController required a single bean, but 2 were found:
	- articleServiceImpl: defined in file ...
	- articleServiceProxy: defined in file ...
```

- ArticleService 를 구현하는 빈이 2개인데 어떤 것을 넣어야 할지 몰라서 오류가 났다.
- 여기서는 @Primary 어노테이션을 사용해서 해결한다.

```java
@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class ArticleServiceProxy implements ArticleService {
    // ...
}
```

- Controller 는 동일하게 ArticleService 객체를 참조하고 있다. (Service 를 사용하는 클라이언트 입장에서는 변화가 없다는 의미에서 중요하다)
- Service 비즈니스 로직과 logging 과 같은 부수적인 작업은 Proxy 로 구분하였다.

### 3. AOP 를 활용한 로깅

- https://engkimbs.tistory.com/746
- AOP 는 객체 중심이 아닌 기능 중심으로 서비스를 제공한다.
- AOP 는 위에서 구현해본 Proxy Pattern 을 활용해서 구현이 되어있다.
- AOP 는 다양한 형태로 구현될 수 있음으로 모두가 프록시 패턴으로 구현되고 있다고 오해하는 하지 말자!. 단지 느낌만 이해하자.

- AOP 용어
  - Aspect : AOP (Aspect Oriented Programming) 객체로서 사용하겠다고 명시
  - Advice : 실제 모듈로서 동작되는 그 일 자체. 여기서는 로깅하는 업무 그 자체를 개념적으로 의미.
  - Weaving : 프록시 객체에 원본 객체를 끼워 넣는 것.
  - Around : 언제 Asepct 를 Weaving 시킬 것인지 그 조건을 명시
  - JoinPoint : Advice가 적용된 위치

- Around 에 조건을 어노테이션 잡아서 특정 어노테이션이 선언되어 있을 때 Aspect 가 실행되도록 구현

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteLog {
}
```

- @Target 어노테이션이 위치할 수 있는 곳을 지정한다. 여기서는 Method 위에만 지정이 가능.
- @Retention 어노테이션은 해당 어노테이션이 어느 시점까지 메모리에 존재하게 하는지를 지정한다.

```java
@Slf4j
@Component
@Aspect
public class ExecuteLogAspect {
  @Around(value = "@annotation(ExecuteLog)")
  public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
    // 작업 시작 시간을 구합니다.
    long start = System.currentTimeMillis();

    // 위빙된 객체의 작업을 진행합니다.
    final Object result = joinPoint.proceed();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();

    String methodName = signature.getName();
    String input = Arrays.toString(signature.getParameterNames());

    String output = result.toString();

    log.info("Method Name : {}, Input : {}, Output : {}, Execute Time : {}", methodName, input, output, (System.currentTimeMillis() - start) + " ms");

    return result;
  }
}
```

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    
    // ...
  
    @ExecuteLog
    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));
    }
    
    // ...
}
```

- Proxy 안 쓰기 위해 Proxy 에 사용되어 있던 @Primary 를 Impl 로 옮기자.

```java
@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class ArticleServiceImpl implements ArticleService {
  // ...
}
```

- 실행 결과를 확인해 보면 Output 부분이 세부적으로 나오지 않고 클래스 명과 헤쉬코드 값이 나온다.
- toString() 을 오버라이딩 하지 않았기 때문에 자바에서 기본으로 제공되는 toString() 함수가 호출되는 것.
- @ToString 어노테이션으로 해결 가능하다.

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

- @ToString 을 활용하는 것은 실무에서는 조금 지양된다. 무한 재귀 호출로 StackOverFlowError 가 발생할 수 있기 때문.

### Optional) 4. Reflection 활용하여 어노테이션으로 정보를 가져와 보자.

- AOP 같은 영역에 특정 정보들을 전달해야할 때는 어노테이션으로 전달하는 방법이 가장 무난하다.
- 여기에서는 type 정보를 어노테이션으로 전달해서 output 을 @ToString 없이 전달 가능하게 해보자.

```java
// ...
public class ArticleServiceImpl implements ArticleService {

    // ...
    
    @ExecuteLog(type = Article.class)
    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not found"));
    }
    
    // ...
}
```

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteLog {
    Class<?> type();
}
```

```java
@Slf4j
@Component
@Aspect
public class ExecuteLogAspect {
    @SuppressWarnings("unchecked")
    @Around(value = "@annotation(ExecuteLog)")
    public <T> Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // 작업 시작 시간을 구합니다.
        long start = System.currentTimeMillis();

        // 위빙된 객체의 작업을 진행합니다.
        final T result = (T) joinPoint.proceed();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // ExecuteLog 어노테이션에 type 값에 들어간 타입을 추론합니다.
        Class<T> clazzType = this.classType(signature.getMethod().getAnnotations());

        String methodName = signature.getName();
        String input = Arrays.toString(signature.getParameterNames());

        String output = this.toString(result);

        log.info("Method Name : {}, Input : {}, Output : {}, Execute Time : {}", methodName, input, output, (System.currentTimeMillis() - start) + " ms");

        return result;
    }

    private <T> String toString(T result) throws Throwable {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Field field : result.getClass().getDeclaredFields()) {
            if(Strings.isBlank(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            sb.append(field.getName()).append("=").append(field.get(result)).append(", ");
        }
        sb.append("]");

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> classType(Annotation[] annotations) throws Throwable {
        Annotation executeLogAnnotation = Arrays.stream(annotations)
                .filter(a -> a.annotationType().getCanonicalName().equals(ExecuteLog.class.getCanonicalName()))
                .findFirst().orElseThrow(() -> new RuntimeException("ExecuteLog Annotation is not existed..."));

        String typeMethodName = "type";
        Method typeMethod = Arrays.stream(executeLogAnnotation.annotationType().getDeclaredMethods())
                .filter(m -> m.getName().equals(typeMethodName))
                .findFirst().orElseThrow(() -> new RuntimeException("type() of ExecuteLog is not existed..."));

        return (Class<T>) typeMethod.invoke(executeLogAnnotation);
    }
}
```

- Reflection 이란 클래스 파일을 읽어서 반대로 인스턴스를 가져올 수 있는 방법

### 6주차 강의

- https://youtu.be/u4kV-0IkHds