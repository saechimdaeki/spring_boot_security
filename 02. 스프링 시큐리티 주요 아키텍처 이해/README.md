# 스프링 시큐리티 주요 아키텍쳐 이해
## 01. DelegatingFilterProxy, FilterChainProxy
### DelegatingFilterProxy
![image](https://user-images.githubusercontent.com/40031858/166101574-0badab25-98c5-4e3a-a37b-8685a0d78a2c.png)
1. 서블릿 필터는 스프링에서 정의된 빈을 주입해서 사용할 수 없음
2. 특정한 이름을 가진 스프링 빈을 찾아 그 빈에게 요청을 위임
   - springSecurityFilterChain 이름으로 생성된 빈을 ApplicationContext에서 찾아 요청을 위임
   - 실제 보안처리를 하지 않음
### FilterChainProxy
![image](https://user-images.githubusercontent.com/40031858/166101601-e2b9f8e2-19a7-4f25-929b-a664d3870b29.png)

1. springSecurityFilterChain의 이름으로 생성되는 필터 빈
2. DelegatingFilterProxy으로 부터 요청을 위임 받고 실제 보안처리
3. 스프링 시큐리티 초기화 시 생성되는 필터들을 관리하고 제어
   - 스프링 시큐리티가 기본적으로 생성하는 필터
   - 설정 클래스에서 API 추가 시 생성되는 필터
4. 사용자의 요청을 필터 순서대로 호출하여 전달
5. 사용자정의 필터를 생성해서 기존의 필터 전,후로 추가 기능
   - 필터의 순서를 잘 정의
6. 마지막 필터까지 인증 및 인가 예외가 발생하지 않으면 보안 통과

### DelegatingFilterProxy , FilterChainProxy
![image](https://user-images.githubusercontent.com/40031858/166101680-f3903f13-d4ef-4e76-9c35-f495c14a2238.png)

## 02. 필터 초기화와 다중 보안 설정
![image](https://user-images.githubusercontent.com/40031858/166125599-2f66d229-b0c8-44aa-a171-c28ff8cbc980.png)

- 설정클래스 별로 보안 기능이 각각 작동
- 설정클래스 별로 RequestMatcher설정
  - http.antMatcher("/admin/**")
- 설정클래스 별로 필터가 생성
- FilterChainProxy가 각 필터들 가지고 있음
- 요청에 따라 RequestMatcher와 매칭되는 필터가 작동하도록 함

![image](https://user-images.githubusercontent.com/40031858/166125624-af3d6615-683d-4fb7-b2f1-d6bb52c299e4.png)

### WebSecurity, HttpSecurity, WebSecurityConfigurerAdapter
![image](https://user-images.githubusercontent.com/40031858/166125643-abe5f472-b68d-45f0-a3af-8c31d606608e.png)

## 03. Authentication
### Authentication
- 당신이 누구인지 증명하는 것
  - 사용자의 인증 정보를 저장하는 토큰 개념
  - 인증 시 id와 password를 담고 인증 검증을 위해 전달되어 사용된다
  - 인증 후 최종 인증결과(user객체, 권한정보)를 담고 SecurityContext에 저장되어 전역적으로 참조가 가능하다
    - Authentication authenticaion = SecurityContextHolder.getContext().getAuthentication()
  - 구조
    - 1) principal : 사용자 아이디 혹은 User 객체를 저장
    - 2) credentials : 사용자 비밀번호
    - 3) authorities : 인증된 사용자의 권한 목록
    - 4) details : 인증 부가 정보
    - 5) Authenticated : 인증 여부

![image](https://user-images.githubusercontent.com/40031858/166129357-53061f05-7f69-4c4f-a3bb-2ef40f707f18.png)


## 04. SecurityContextHolder , SecurityContext
- `SecurityContext`
  - Authentication 객체가 저장되는 보관소로 필요 시 언제든지 Authentication객체를 꺼내어 쓸 수 있도록 제공되는 클래스
  - ThreadLocal에 저장되어 아무 곳에서나 참조가 가능하도록 설계함
  - 인증이 완료되면 HttpSession에 저장되어 애플리케이션 전반에 걸쳐 전역적인 참조가 가능하다
- `SecurityContextHolder`
  - SecurityContext 객체 저장 방식
    - MODE_THREADLOCAL : 스레드당 SecurityContext 객체를 할당, 기본값
    - MODE_INHERITABLETHREADLOCAL : 메인 스레드와 자식 스레드에 관하여 동일한 SecurityContext를 유지
    - MODE_GLOBAL : 응용 프로그램에서 단 하나의 SecurityContext를 저장한다
  - SecurityContextHolder.clearContext() : SecurityContext 기존 정보 초기화
- `Authentication authentication = SecurityContextHolder.getContext().getAuthentication()`

![image](https://user-images.githubusercontent.com/40031858/166129617-257a526a-3c44-4746-8437-a7778b563e56.png)