# 멋사 미션형 프로젝트
> 지난 미니 프로젝트에서는 기초를 다졌다면, 이번 미션형 프로젝트에서는 기초를 응용해보는 시간입니다.
> 이번 미션형 프로젝트 또한 요구사항 정의서 기반의 백엔드 기능 구현 미션의 형태로, 단계적으로 서비스를 만들어봅니다. 

# 멋사 마켓 ERD

![멋사마켓ERD](https://github.com/likelion-backend-5th/Project_1_KimMinGyun/assets/86220874/8d27da88-7754-4de1-8dff-4ee95fbafd19)
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
    
  ### Postman Test 정리
</summary>
===========================================================================
<details>
    <summary>
    
  #### a. 회원가입(Register Member)
  </summary>
<div markdown="1">

</div>
</details>
<details>
  <summary>
      
  #### b. 로그인(Login)
  </summary>
<div markdown="1">
  
</div>
</details>
<details>
  <summary>
      
