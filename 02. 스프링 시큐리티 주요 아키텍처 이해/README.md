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