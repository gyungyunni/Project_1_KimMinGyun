## 멋사 미션형 프로젝트
> 지난 미니 프로젝트에서는 기초를 다졌다면, 이번 미션형 프로젝트에서는 기초를 응용해보는 시간입니다.
> 이번 미션형 프로젝트 또한 요구사항 정의서 기반의 백엔드 기능 구현 미션의 형태로, 단계적으로 서비스를 만들어봅니다. 

## 멋사 마켓 ERD

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/8d27da88-7754-4de1-8dff-4ee95fbafd19"/>

> UserEntity가 추가됨으로써 ERD를 다시 작성해봤습니다.

<details>
<summary>  
      
  ### 기술 스택 
</summary>

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=red"><img src="https://img.shields.io/badge/intellijidea-000000F?style=for-the-badge&logo=Spring Boot&logoColor=black">   
</details>
===========================================================================
<details>
   
<summary>  
      
  ### Mission by date
</summary>

### Day1

```sh
1. 사용자는 **회원가입**을 진행할 수 있다.
    - 회원가입에 필요한 정보는 아이디와 비밀번호가 필수이다.
    - 부수적으로 전화번호, 이메일, 주소 정보를 기입할 수 있다.
    - 이에 필요한 사용자 Entity는 직접 작성하도록 한다.
    
2. **아이디 비밀번호**를 통해 로그인을 할 수 있어야 한다.
    
    
3. 아이디 비밀번호를 통해 로그인에 성공하면, **JWT가 발급**된다. 이 JWT를 소유하고 있을 경우 **인증**이 필요한 서비스에 접근이 가능해 진다.
    - 인증이 필요한 서비스는 추후(미션 후반부) 정의한다.
    
4. JWT를 받은 서비스는 **사용자가 누구인지** 사용자 **Entity를 기준**으로 정확하게 판단할 수 있어야 한다.
```

### (요구사항 구현하기 위해 집중한 부분, 변경하거나 추가한 부분)
* 1 
    * 필수 정보인 아이디, 비밀번호에 부수적으로 전화번호, 이메일, 주소 정보를 기입해야해서 UserDetails를 implements한 
      CustomUserDetails를 작성하였다.
    * 이에 필요한 UserEntity를 작성하였다.
    * 필수 정보인 username 과 password는 nullable = false를 하였고, username은 중복이 되면 안되기 때문에 unique 속성도 부여하였다.
    * 회원가입은 TokenController의 Post token/register로 진행할 수 있다.
 
 * 2 
    * 아이디 / 비밀번호로 로그인하기 위해서 JwtRequestDto를 활용하였다.
      
 * 3
    * 로그인에 성공하면 JWT가 발급되는데, TokenController의 Post token/issue로 진행할 수 있다.
    * username과 password가 일치하면 token을 생성하고 부여해준다
      
 * 4 
    * TokenController의 Post token/check로 사용자가 누구인지 확인할 수 있다.
    * Authorization bearer 부분에 토큰을 넣어줌으로써 username을 반환한다.        
            
===========================================================================

### Day2

```sh
1. 아이디와 비밀번호를 필요로 했던 테이블들은 실제 사용자 Record에 대응되도록 ERD를 수정하자.
    - ERD 수정과 함께 해당 정보를 적당히 표현할 수 있도록 Entity를 재작성하자.
    - 그리고 ORM의 기능을 충실히 사용할 수 있도록 어노테이션을 활용한다.
    
2. 다른 작성한 Entity도 변경을 진행한다.
    - 서로 참조하고 있는 테이블 관계가 있다면, 해당 사항이 표현될 수 있도록 Entity를 재작성한다.
```

* 1(UserEntity) 
    * 맨 위에 첨부해둔 ERD에 맞춰 엔티티를 수정하였다.
    * BaseEntity를 상속받은 UserEntity를 만들어줌으로써 createdAt, updatedAt 속성이 추가되었다.
    * 엔티티들과 연관을 맺을때 생기는 중간 매핑 테이블을 없애기위해 @JoinColumn을 이용하여 어떤 컬럼으로 조인을 할지 지정해주었다
    * 그리고 cascade-remove를 설정해 user 데이터가 지워질 때, 그 user가 포함된 데이터를 같이 지워지도록 하였다.

* 2(OtherEntities) 
    * 맨 위에 첨부해둔 ERD에 맞춰 엔티티를 수정하였다.
    * UserEntity에서 @OneToMany를 맺어주었지만, 다른 엔티티에서도 @ManyToOne을 이용하여 양방향으로 매핑을 해주겠다.
    * Many 쪽에 FK가 생기기 때문에, 어느 엔티티에서 연관 엔티티가 필요한지를 생각하고 관계를 맺어주면 좋다.
    * 이 프로젝트에서는 보통 판매 게시글이나 댓글, 네고 글 에서 유저를 찾아서 @ManyToOne이 유용하지만, 양방향으로 관계를 맺어주었다.
    * @ManyToOne을 할 때, CascadeType.PERSIST, CascadeType.MERGE 기능도 추가하여 각각의 엔티티에서 삽입이나, 수정을 할 때
      One 쪽 엔티티에도 반영이 되게 설정해주었다.
      
===========================================================================

### Day3
```sh
1. 본래 “누구든지 열람할 수 있다”의 기능 목록은 사용자가 **인증하지 않은 상태**에서 사용할 수 있도록 한다.
    - 등록된 물품 정보는 누구든지 열람할 수 있다.
    - 등록된 댓글은 누구든지 열람할 수 있다.
    - 기타 기능들
    
2. 작성자와 비밀번호를 포함하는 데이터는 **인증된 사용자만 사용**할 수 있도록 한다.
    - 이때 해당하는 기능에 포함되는 아이디 비밀번호 정보는, 1일차에 새로 작성한 사용자 Entity와의 관계로 대체한다.
        - 물품 정보 등록 → 물품 정보와 사용자 관계 설정
        - 댓글 등록 → 댓글과 사용자 관계 설정
        - 기타 등등
    - 누구든지 중고 거래를 목적으로 물품에 대한 정보를 등록할 수 있다.
    - 등록된 물품에 대한 질문을 위하여 댓글을 등록할 수 있다.
    - 등록된 물품에 대하여 구매 제안을 등록할 수 있다.
    - 기타 기능들
```

* 1
    * permitAll()에 
      "/token/issue", "/token/register"
      HttpMethod.GET, "/api/mutsamarket/items", "/api/mutsamarket/items/all", "/api/mutsamarket/items/{itemId}",
                      "/api/mutsamarket/items/{itemId}/comments"
      을 추가해줌으로써 로그인, 회원가입, 판매게시글 조회 3가지, 댓글 조회는 별 다른 인증 없이 누구든지 열람이 가능하다.

* 2
    * read를 제외한 다른 대부분의 기능들 create, update, delete는 인증된 사용자만 사용할 수 있도록 하였다
    * 예를들어, 등록은 누구든지 할 수 있지만 삭제, 수정 등을 할 때는 그 객체(물품, 댓글, 네고 등)의 등록자만이 할 수 있다
    * 또한, 네고를 조회할 때도 물품 정보 등록자는 모든 네고를 확인할 수 있고, 네고 작성자는 본인이 작성한 네고만 확인할 수 있다.

</details>
===========================================================================

<details>
<summary>
    
  ### Postman Test
</summary>


(사진을 클릭하시면 크게 볼 수 있습니다)

### 회원가입/로그인

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/d2beb45b-a411-4461-809d-cebd9a00d461"/>

* 회원가입 정보를 Json 형식으로 보내면 회원가입


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/9c1986da-8233-4b9d-80d9-e45ac512a12b"/>

* username과 password를 JSON 형식으로 보내면 로그인 하면서 토큰을 발급받을 수 있다
    * mingyun의 토큰은 eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaW5neXVuIiwiaWF0IjoxNjkwODk4MzgyLCJleHAiOjE2OTA5MDE5ODJ9.5EsDv-Yx-q-YHuJImUPU0ait_sIIEEI8P69rM1_uAxXB3kUUZXbPm9jztr67TCrr6fGzRfIuVTtM4QdWmTJHaw
    * 미리 JpaUserDetailsManager에 만들어 놓은 user, user1이 있는데 test를 위해 user의 토큰도 받아서 두 명의 사용자를 가정해 테스트 해보겠다
    * user1의 토큰은 eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MDg5ODg5MCwiZXhwIjoxNjkwOTAyNDkwfQ.LufEgCwSMzrANDEH2xSfJgq1W01WXIeqrdkIMCPkWyXW8pJLS1GkmafoxQuPbfPXWf9qrrmjmBTO1sF14CQv4Q
    * 테스트할 때 인증이 필요한 기능들은 계속 Authorization - Bearer Token 부분에 토큰을 넣어주면서 할 것이다. 기능에 따라 번갈아가며 넣을 것이다.


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/060dae0f-898a-4166-92df-68d3557b07bb"/>

* 토큰을 Authorization - Bearer Token의 넣어주고 요청을 보내면 username도 확인할 수 있다 

===========================================================================

### SalesItem 기능

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/6b55d9f8-5d48-4b81-bf2c-041231b99bae"/>

* 포스트맨의 컬렉션에서는 로그인을 하면 Authorization - Bearer Token에 자동으로 토큰 값이 넣어지기도 하는데, 일단 수동으로 넣어주고 요청을 보내겠다
    * 일단 mingyun의 토큰을 넣어줬다 (mingyun으로 로그인 한 것)
    * 그리고 item 판매 게시글을 2개 올리는 테스트를 하였다


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/47ffc706-1f3a-4e06-bf1d-1dbd56ee9dac"/>

* read
    * read는 토큰을 안넣어줘도 조회를 할 수 있게 해놨다
    * 왼쪽에 get 요청 3개는 차례대로 단일 조회, 전체 조회, 페이지 조회이다.
    * 대표적으로 전체 조회만 사진으로 올려두었다. 3가지 조회가 다 잘되는 것을 확인하였다.


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/ff2bb8df-dd30-44a6-aca6-99c9eeb4453e"/>

* delete
    * delete는 토큰을 넣어줘야한다.
    * 토큰을 통해 itemId의 작성자와 로그인한 user가 일치하면 삭제를 할 수 있게 하였다.
    * 판매 게시글 작성자가 아닌 user1의 토큰을 넣으면은 삭제가 되지 않는 것을 확인할 수 있다.


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/15d3df43-c2fd-4d7c-9c34-c3e0c650e8c9"/>

* update
    * 마찬가지로 토큰으로 인증을 해서 작성자만이 수정을 할 수 있다.

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/87d33aa1-158b-4467-815d-5ee88feddc54"/>

* Put Image
    * 사진을 넣을 수 있다. 서버에 폴더가 생기고 그 Url을 넣어주었다.


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/3a24df96-b152-4706-8900-35af4b3169ef"/>

* read
    * put, delete 등을 하고 다시 조회한 결과 기능들이 잘 수행된 것을 확인할 수 있다
    
===========================================================================

### Comment 기능

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/b1210428-60c3-45cf-9c34-da11e1fadb68"/>

* post 댓글
    * 댓글은 작성자든 구매하려는 사람이든 달 수 있게 하였다.
    * 댓글의 답글은 작성자만 달 수 있게 하였다.
    * 지금 테스트에서는 user1의 토큰으로 달았다. 삭제를 위해 2개 달았다. 


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/e8d09b64-a29b-4a83-84b9-d86323eb2a4f"/>
      
* read
    * 페이지 조회로 구현하였다. itemId에 해당하는 댓글들을 보여준다.
    * 토큰 인증이 없어도 된다. 


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/4cc4ccf4-4878-4830-ac99-2e5516bdfbcb"/>

* delete
    * 해당 댓글 작성자의 토큰(여기선 user1)을 넣어줘야 삭제를 할 수 있다.
    * itemId: 1, commentId: 2 의 댓글을 삭제하였다.


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/c503b083-b641-4b90-8e18-90e723922821"/>

* update 댓글
    *  해당 댓글 작성자가 수정을 할 수 있다.
    *  itemId: 1, commentId: 1 의 댓글을 수정하였다.  



<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/d8d5b5c4-3c80-4bf8-ad37-143831d4704d"/>

* post Reply
    * 아이템 판매글 작성자만이 달 수 있다.
    * 1번 아이템 1번 댓글에 대해 답글을 달았다.

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/f62e7678-9de5-4533-987e-e95de13705db"/>
   
* read
    * read 결과 post, delete, update가 정상 동작한 것을 확인할 수 있다.
 
===========================================================================

### Negotiation 기능

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/adef2ff0-0345-44a8-92e9-2f0597b40df2"/>

* post Nego
    * Nego 에서는 또 다른 user의 토큰을 발급받아 총 3명의 토큰으로 test를 할 것이다.
    * 일단 user1, user2 의 토큰으로 각각 2개씩 Nego를 post 하였다

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/d212e7e9-a31b-47aa-bb5f-54abd2f93ae4"/>

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/c1933057-fdf7-4858-ae74-9861620c062d"/>

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/aa7b769f-c023-4b0c-b3f1-8b9995466edf"/>

 
* read
    * read는 세 가지 경우를 확인해야 한다.
    * 위에서부터 차례로 판매글 작성자의 토큰, user1의 토큰, user2의 토큰으로 read를 한 결과이다.
    * 판매글 작성자는 모든 네고를 확인할 수 있다.
    * 각각의 user들은 자신의 네고만 확인할 수 있다.
    * read의 결과로써 네고했던 가격과 status를 확인할 수 있다.

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/bce7ed2a-af1f-4a67-8da7-0c49852b5e8c"/>

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/657cc461-1fa7-46ee-bd8c-9092f321494b"/>

* update(1)
    * update도 세가지 경우가 있는데, 일단 첫번째 경우이다.
    * 네고를 했던 사람만이 가격을 수정할 수 있다.(다른 토큰으로 할 시 에러 발생)
    * read로 결과를 확인하였다. 네고 가격이 잘 바뀐 것을 확인하였다. (편의를 위해 판매 글 작성자의 토큰으로 read 수행)


<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/c3cd4c43-56f0-4e28-b8ff-d309e1c4c192"/>

* delete
    * 또 다른 update 확인에 앞서 delete를 했다.
    * 마찬가지로 네고를 한 사람의 토큰을 넣어줘야 삭제가 된다

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/2a7e7613-2b30-4b3c-9e22-17e8edec72d4"/>

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/79f5cf7d-f18a-4706-9c08-83a9c503dbc1"/>
 
* update(2)
    * 네고를 받은 작성자는 status를 바꿔줄 수 있다.
    * 수락 or 거절로 바꿀 수 있는데, 1번 네고에 대해서는 수락을 하였고 2번 네고에 대해서는 거절, 3번 네고에 대해서는 그대로 두었다.
    * 조회 결과 status가 잘 바뀐 것을 확인할 수 있다.
 
<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/01285fe9-2c56-4d09-a6a3-567c5ffa9df6"/>

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/e9442f94-99e9-456d-9eec-7bb8dff6547b"/>

  
* update(3)
    * 네고를 한 유저는 status가 "수락" 상태인 것에 한해 구매 확정을 할 수 있다.
    * 구매 확정을 함에 따라 나머지의 제안들의 status가 "거절로 바뀌는 것을 확인할 수 있다.
 
</details>
