# 멋사 미션형 프로젝트
> 지난 미니 프로젝트에서는 기초를 다졌다면, 이번 미션형 프로젝트에서는 기초를 응용해보는 시간입니다.
> 이번 미션형 프로젝트 또한 요구사항 정의서 기반의 백엔드 기능 구현 미션의 형태로, 단계적으로 서비스를 만들어봅니다. 

# 멋사 마켓 ERD

<img width="60%" src="https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/8d27da88-7754-4de1-8dff-4ee95fbafd19"/>

> UserEntity가 추가됨으로써 ERD를 다시 작성해봤습니다.


## DAY 1 / 인증 만들기

```sh
1. 사용자는 회원가입을 진행할 수 있다.
    a. 회원가입에 필요한 정보는 아이디와 비밀번호가 필수이다.
    b. 부수적으로 전화번호, 이메일, 주소 정보를 기입할 수 있다.
    c. 이에 필요한 사용자 Entity는 직접 작성하도록 한다.
2. 아이디 비밀번호를 통해 로그인을 할 수 있어야 한다.. 

3. 아이디 비밀번호를 통해 로그인에 성공하면, JWT가 발급된다. 이 JWT를 소유하고 있을 경우 인증이 필요한 서비스에 접근이 가능해 진다.. 
    a. 인증이 필요한 서비스는 추후(미션 후반부) 정의한다.

4. JWT를 받은 서비스는 사용자가 누구인지 사용자 Entity를 기준으로 정확하게 판단할 수 있어야 한다.
   
```
### (요구사항 구현하기 위해 집중한 부분, 변경하거나 추가한 부분)
* SalesItem Entity(중고 물품 정보)
  * BaseEntity에 EntityListener(Auditable 인터페이스)를 연결하고 엔티티 클래스가 상속 받도록 하였다.
  * createdAt, updatedAt 정보같은 경우는 공통적으로 있으면 좋다고 생각하여서 구현하였다. 
  * Comment 엔티티와 Negotiation 엔티티는 이 엔티티와 1:N 관계이므로 @OneToMany를 이용하여 관계를 맺어주었다.
 
(기능 부분 - 중고 물품 관리는 ApiController와 MarketService를 이용하였다.)
* 1 (enrollSalesItem)
    * 물품에 대한 정보를 등록할 때 꼭 필요한 내용인 제목, 설명, 최소 가격, 작성자를 받기 위해 SalesItemEnrollDto를 작성하였다.
    * 꼭 필요한 정보는 dto의 유효성 검사 기능을 이용하여 @NotNull을 붙여주었다. 가격에는 @Min도 붙여주어 최소 0원으로 설정하였다.
    * 1.b는 dto에 포함된 비밀번호를 가져와서 set 해주었다
    * 1.c는 상품이 등록될 때 setStatus를 통해 "판매중" 상태로 변경되게 해주었다.
          
* 2 (readItem, readItemAll, readPageItem)
    * readItem()을 통해 itemId를 PathVariable로 받아서 해당 itemId에 해당하는 데이터를 찾기 위해 쿼리메소드 findById를 사용하였다.
      SalesItemReadDto를 이용하여 원하는 정보만 조회되게 하였다.
    * readItemAll()은 salesItemRepository.findAll()을 통해 salesItemList에 넣고 비어있는지 확인을 해주고, 
      비어있지 않다면 dto로 변환하고 return 해주었다.  
    * 페이지 조회도 page와 size를 @RequestParam으로 받아서(기본값 설정) pageRequest.of()를 이용해 pageable을 만들어주었다.
    * 그리고 pageable은 jpa에서 제공하는 findAll을 이용하여 Page<SalesItem> 객체를 반환하고 SalesItemReadDto를 이용하여 원하는 정보만 
      조회되게 하였다.
      
* 3,4 (updateSalesItem, updateMarketImage)
    * update를 하기 위해 itemId와 dto(password 포함)를 받아주었다.
    * itemId, writer, password가 일치하는 salesItem을 쿼리메서드로 찾아서 업데이트 해준다
    * 이미지는 수업시간에 배운 코드를 이용하였다. 마찬가지로 writer, password, itemId가 일치하는 salesItem에 사진을 업데이트 해주었다.
 
* 5  (deleteItem)
    * service 쪽에서 삭제 요청할때 받는 itemId, writer, password가 일치하는지 쿼리메소드로 검사하고 삭제할 게시글이 있으면 삭제를 하고 true를 반환한다. 삭제할 것이 없으면 false를 반환한다.
    * 그래서 삭제를 했으면 "물품을 삭제했습니다"를 응답하고 삭제할 것이 없으면 throw new ResponseStatusException(HttpStatus.NOT_FOUND)를 응답한다.   

## 중고 물품 댓글 요구사항

```sh
1. 등록된 물품에 대한 질문을 위하여 댓글을 등록할 수 있다. 
    a. 이때 반드시 포함되어야 하는 내용은 대상 물품, 댓글 내용, 작성자이다.
    b. 또한 댓글을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
2. 등록된 댓글은 누구든지 열람할 수 있다. 
    a. 페이지 단위 조회가 가능하다.
3. 등록된 댓글은 수정이 가능하다. 
    a. 이때, 댓글이 등록될 때 추가한 비밀번호를 첨부해야 한다.
4. 등록된 댓글은 삭제가 가능하다. 
    a. 이때, 댓글이 등록될 때 추가한 비밀번호를 첨부해야 한다.
5. 댓글에는 초기에 비워져 있는 답글 항목이 존재한다. 
    a. 만약 댓글이 등록된 대상 물품을 등록한 사람일 경우, 물품을 등록할 때 사용한 비밀번호를 첨부할 경우 답글 항목을 수정할 수 있다.
    b. 답글은 댓글에 포함된 공개 정보이다.
```
### (요구사항 구현하기 위해 집중한 부분, 변경하거나 추가한 부분)
* Comment Entity(댓글 정보)
  * BaseEntity에 EntityListener(Auditable 인터페이스)를 연결하고 엔티티 클래스가 상속 받도록 하였다.
  * createdAt, updatedAt 정보같은 경우는 공통적으로 있으면 좋다고 생각하여서 구현하였다.
  * Comment 엔티티와 SalesItem 엔티티는 N:1 관계이므로 @ManyToOne를 이용하여 관계를 맺어주었다.
 
 
(기능 부분 - 중고 물품 관리는 CommentController와 CommentService를 이용하였다.)
* 1 (enrollComment)
    * 물품, 댓글 내용, 작성자를 꼭 포함하기 위해서 SalesItemEnrollDto(NotNull)를 이용하여 받아주었다.
    * 그리고 SalesItem 엔티티와 1:N 관계이기 때문에 setSalesItem(salesItemRepository.findById(itemId));으로 itemId에 해당하는 SalesItem을 넣어주었다
      이렇게 하면 Comment 엔티티에서 itemId, itemId에 해당하는 commentId 등의 정보를 편리하게 이용할 수 있다.
      
* 2 (readCommentsPage)
    * 댓글 조회는 페이지 조회로 구현하였다.
    * RequestParam으로 page를 받고, PathVariable로 itemId를 받아서, 25개의 사이즈로 페이지가 나눠져 있는데 원하는 페이지를 조회할 수 있다.
    * commentRepository에서 같은 itemId에 해당하는 댓글들을 모두 Page객체에 넣어주고 CommentsReadDto 형식에 맞게 리턴되게 하였다.
 
* 3  (updateComment)
    * commentRepository에서 쿼리메소드 findBySalesItemIdAndIdAndWriterAndPassword(itemId, id,  dto.getWriter(), dto.getPassword())를 이용하여
      물건의 id, 댓글의 id, 댓글 작성자, 댓글 작성자의 password가 일치하면 댓글의 content를 업데이트하게 해주었다.

* 4  (deleteComment)
    * PathVariable로 받은 itemId, commentId, RequestBody로 받은 CommentDeleteDto(댓글 작성자, password 등의 정보)를 
      쿼리메소드에 다음과 같이 넣어주어 commentRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId,id, writer, password); 일치하는게 있으면 삭제하도록 하였다.

* 5  (addReply)
    * addReply에서는 두가지를 구현하였다. 1. 첫 답변일때, 2. 답변을 수정할 때
    * 먼저, 첫 답변일 때는 PathVariable로 받은 itemId와 commentId를 가지고 쿼리메소드 findBySalesItemIdAndId(itemId, commentId)를 이용하여 commentRepository에서 해당하는 댓글을 가져온다
      그 댓글에 답글이 있는지를 검사한다(첫 답변이면 getReply()를 했을 때 null이다) Reply가 댓글을 조회할때 보여야하기 때문에 commentRepository.findBySalesItemIdAndId(itemId, commentId)를 
      이용하여 해당하는 댓글을 찾고 setReply를 이용하여 답글을 넣어주었다.
    * 답변을 수정하는 경우도 똑같은 방식인데, 5.a의 조건 때문에 salesItemRepository.findById(itemId).getWriter().equals(dto.getWriter()) 게시글의 작성자가 일치하는지를 검사한다.
      그리고 return 값에 따라 답변이 추가된건지(1), 답변이 수정된건지(2)를 구별해주었다.
      
<details>
<summary>
    
  ### Postman Test
</summary>
===========================================================================

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





      
