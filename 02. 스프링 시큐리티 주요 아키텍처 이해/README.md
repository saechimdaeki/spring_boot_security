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

## 05. SecurityContextPersistenceFilter
- `SecurityContext객체의 생성, 저장 , 조회`
  - 익명 사용자
    - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder에 저장
    - AnonymousAuthenticationFilter에서 AnonymousAuthenticationToken 객체를 SecurityContext에 저장
  - 인증 시 
    - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder에 저장
    - UsernamePasswordAuthenticationFilter에서 인증 성공 후 SecurityContext에 UsernamePasswordAuthentication 객체를 SecurityContext에 저장
    - 인증이 최종 완료되면 Session에 SecurityContext를 저장
  - 인증 후
    - Session에서 SecurityContext 꺼내어 SecurityContextHolder에서 저장
    - SecurityContext안에 Authentication 객체가 존재하면 계속 인증을 유지한다
  - 최종 응답 시 공통
    - SecurityContextHolder.clearContext()

![image](https://user-images.githubusercontent.com/40031858/166131304-9b833da4-be31-441f-861e-0e178fa76194.png)

![image](https://user-images.githubusercontent.com/40031858/166131308-1adbd9dc-76d1-4731-9c7b-a3e4379affde.png)

![image](https://user-images.githubusercontent.com/40031858/166131317-884f30fe-45ae-4702-b5ac-8e70c1eeccc0.png)

### Authentication Flow
![image](https://user-images.githubusercontent.com/40031858/166131701-3425c525-9b77-4d4e-a613-d02fecce66fa.png)

## 06. AuthenticationManager
![image](https://user-images.githubusercontent.com/40031858/166135994-11d63167-550e-414d-b768-89106834ef18.png)

- AuthenticationProvider 목록 중에서 인증 처리 요건에 맞는 AuthenticationProvider를 찾아 인증처리를 위임한다
- 부모 ProviderManager를 설정하여 AuthenticationProvider를 계속 탐색 할 수 있다.

### AuthenticationManager 응용
![image](https://user-images.githubusercontent.com/40031858/166136030-e713cc7a-42b9-4a32-bd01-85bb28f76bbc.png)

- Linked 형태로 부모와 자식간의 관계를 형성할 수 있다
- 자식에서 적절한 AuthenticationProvider를 찾지 못할 경우 계속 부모로 탐색하여 찾는 과정을 반복한다
- AuthenticationManagerBuilder를 사용해서 스프링 시큐리티의 초기화 과정에서 설정한 기본 Parent관계를 변경해야 권한 필터에서 재 인증 시 모든 AuthenticationProvider를 탐색할 수 있다.

## 07. AuthenticationProvider
![image](https://user-images.githubusercontent.com/40031858/166136164-8868e0bd-823f-4476-9502-2a9f802e5a63.png)

## 08. Authorization, FilterSecurityInterceptor
### Authroization
- 당신에게 무엇이 허가되었는지 증명하는 것

![image](https://user-images.githubusercontent.com/40031858/166136223-5d4f2509-9dcc-4fa3-9793-5a75d54e0f65.png)

- 스프링 시큐리티가 지원하는 권한 계층
  - 웹 계층
    - URL 요청에 따른 메뉴 혹은 화면단위의 레벨 보안
  - 서비스 계층
    - 화면 단위가 아닌 메소드 같은 기능 단위의 레벨 보안
  - 도메인 계층(Access Control List, 접그제어목록)
    - 객체 단위의 레벨 보안

### FilterSecurityInterceptor
- 마지막에 위치한 필터로써 인증된 사용자에 대하여 특정 요청의 승인/거부 여부를 최종적으로 결정
- 인증객체 없이 보호자원에 접근을 시도할 경우 AuthenticationException을 발생
- 인증 후 자원에 접근 가능한 권한이 존재하지 않을 경우 AccessDeniedException을 발생
- 권한 제어 방식 중 HTTP 자원의 보안을 처리하는 필터
- 권한 처리를 AccessDecisionManager에게 맡김

![image](https://user-images.githubusercontent.com/40031858/166136273-71b974b2-41e6-46ff-88bf-37a699abc49e.png)

![image](https://user-images.githubusercontent.com/40031858/166136290-275bbfd2-8b92-4a3b-93d9-0e87e735c4fe.png)

## 09. AccessDecisionManager, AccessDecisionVoter
### AccessDecisionManager
- 인증 정보, 요청정보, 권한정보를 이용해서 사용자의 자원접근을 허용할 것인지 거부할 것인지를 최종 결정하는 주체
- 여러개의 Voter들을 가질 수 있으면 Voter들로부터 접근허용, 거부, 보류에 해당하는 각각의 값을 리턴받고 판단 및 결정
- 최종 접근 거부 시 예외 발생
- 접근결정의 세가지 유형
  - `AffirmativeBased`:
    - 여러개의 Voter 클래스 중 하나라도 접근 허가로 결론을 내면 접근 허가로 판단하다.
  - `ConsensusBased`:
    - 다수표(승인 및 거부)에 의해 최종 결정을 판단한다
    - 동수일경우 기본은 접근허가이니 allowIfEqualGrantedDeniedDecisions을 false로 설정할 경우 접근거부로 결정된다
  - `UnanimousBased`:
    - 모든 보터가 만장일치로 접근을 승인해야 하며 그렇지 않은 경우 접근을 거부한다.

### AccessDecisionVoter
- 판단을 심사하는 것(위원)
- Voter가 권한 부여 과정에서 판단하는 자료
  - Authentication - 인증 정보 (user)
  - FilterInvocation - 요청 정보 (antMatcher("/user"))
  - ConfigAttributes - 권한 정보 (hasRole("USER"))
- 결정 방식
  - ACCESS_GRANTED : 접근 허용(1)
  - ACCESS_DENIED : 접근 거부(0)
  - ACCESS_ABSTRAIN : 접근 보류(-1)
    - Voter가 해당 타입의 요청에 대해 결정을 내릴 수 없는 경우

![image](https://user-images.githubusercontent.com/40031858/166149751-715358b9-7f16-4684-b5fc-e80da98e123c.png)

![image](https://user-images.githubusercontent.com/40031858/166220385-98e70726-b634-4b1b-aac0-1a5d41a99439.png)
