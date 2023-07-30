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
      
 
## 구매 제안 요구사항

```sh
1. 등록된 물품에 대하여 구매 제안을 등록할 수 있다. 
    a. 이때 반드시 포함되어야 하는 내용은 대상 물품, 제안 가격, 작성자이다.
    b. 또한 구매 제안을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
    c. 구매 제안이 등록될 때, 제안의 상태는 제안 상태가 된다.
2. 구매 제안은 대상 물품의 주인과 등록한 사용자만 조회할 수 있다.
    a. 대상 물품의 주인은, 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다. 이때 물품에 등록된 모든 구매 제안이 확인 가능하다. 페이지 기능을 지원한다.
    b. 등록한 사용자는, 조회를 위해서 자신이 사용한 작성자와 비밀번호를 첨부해야 한다. 이때 자신이 등록한 구매 제안만 확인이 가능하다. 페이지 기능을 지원한다.
3. 등록된 제안은 수정이 가능하다. 
    a. 이때, 제안이 등록될때 추가한 작성자와 비밀번호를 첨부해야 한다.
4. 등록된 제안은 삭제가 가능하다. 
    a. 이때, 제안이 등록될때 추가한 작성자와 비밀번호를 첨부해야 한다.
5. 대상 물품의 주인은 구매 제안을 수락할 수 있다. 
    a. 이를 위해서 제안의 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    b. 이때 구매 제안의 상태는 수락이 된다.
6. 대상 물품의 주인은 구매 제안을 거절할 수 있다. 
    a. 이를 위해서 제안의 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    b. 이때 구매 제안의 상태는 거절이 된다.
7. 구매 제안을 등록한 사용자는, 자신이 등록한 제안이 수락 상태일 경우, 구매 확정을 할 수 있다. 
    a. 이를 위해서 제안을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    b. 이때 구매 제안의 상태는 확정 상태가 된다.
    c. 구매 제안이 확정될 경우, 대상 물품의 상태는 판매 완료가 된다.
    d. 구매 제안이 확정될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절이 된다.
```
### (요구사항 구현하기 위해 집중한 부분, 변경하거나 추가한 부분)
* Negotation Entity(네고 정보)
  * BaseEntity에 EntityListener(Auditable 인터페이스)를 연결하고 엔티티 클래스가 상속 받도록 하였다.
  * createdAt, updatedAt 정보같은 경우는 공통적으로 있으면 좋다고 생각하여서 구현하였다.
  * Negotiation 엔티티와 SalesItem 엔티티는 N:1 관계이므로 @ManyToOne를 이용하여 관계를 맺어주었다.
 
 
(기능 부분 - 중고 물품 관리는 CommentController와 CommentService를 이용하였다.)
* 1 (enrollNegotiation)
    * 대상 물품, 제안 가격, 작성자를 꼭 포함하기 위해서 SalesItemEnrollDto(NotNull)를 이용하여 받아주었다.
    * 구매 제안이 등록될 때, 제안의 상태는 제안 상태가 되도록 해주었다.
      
* 2 (readNegoPage)
    * 네고 조회는 페이지 조회로 구현하였다.
    * 대상 물품의 주인, 네고를 등록한 사용자 별로 조회할 수 있게 구현하였다.
    * 두가지 if로 writer와 password가 대상 물품의 주인의 것인지, 네고를 등록한 사용자의 것인지 구별할 수 있다.
    * 대상 물품의 주인의 것이면 모든 구매 제안을 확인 가능하고, 네고를 등록한 사용자의 것이면 자신이 등록한 구매 제안만 확인할 수 있게 하였다.
 
* 3, 5, 6, 7  (updateNegotiation)
    * 요구사항 3, 5, 6, 7을 하나의 메소드에 구현하였다.
    * 3번 요구사항은 네고를 수정하는 것으로 EndPoints를 보면 네고를 수정할 때는 작성자, 비밀번호, 제안가격의 정보를 보내고 있다.
      그래서, Status의 정보는 안보내도 되는 것으로 판단해 dto.getStatus() == null인 경우에 네고 수정을 하도록 하였다.
      negotiationRepository.findBySalesItemIdAndIdAndWriterAndPassword 를 이용하여 판매 아이템, 네고글 번호, 네고 작성자, 작성자 비밀번호와 일치하는
      네고 글을 가져와서 업뎃을 해주고 "제안이 수정되었습니다"를 응답 하게 해주었다.
    * 5,6 번은 한번에 구현하였다. 아이템 판매글 작성자가 요청을 한다.
      Status의 정보가 if((dto.getStatus().equals(오케이)) || (dto.getStatus().equals(no))로 일차적으로 걸러내고,
      아이템 판매글 작성자가 맞는지 확인하기 위해서 findBySalesItemIdAndIdAndSalesItemWriterAndSalesItemPassword를 이용하여 한번 더 검사를 한다.
      그래서 대상 물품의 주인이 구매 네고를 수락했는지 거절했는지에 따라 업데이트 해주고 "제안의 상태가 변경되었습니다"를 응답해준다
    * 7번은 구매 제안을 등록한 사용자는, 자신이 등록한 제안이 수락 상태일 경우, 구매 확정을 할 수 있다라는 요구사항이다.
      그래서 dto를 통해 확정 요청을 보내면 이 메소드를 실행하기 위해 받은 dto의 status를 검사해준다. if(dto.getStatus().equals("확정")) 
      그리고 negotiationRepository.findBySalesItemIdAndIdAndWriterAndPasswordAndStatus(itemId, Math.toIntExact(id), dto.getWriter(), dto.getPassword(), ok)
      쿼리메서드를 이용하여 "확정"을 요청한 사람이 네고 요청을 한 작성자가 맞는지, 비밀번호가 맞는지, 해당 판매 아이템이 맞는지, 그 아이템의 상태가 "수락"이 맞는지를 검사한다.
      (그런데 이걸 가져온게 isEmpty이면 404 에러를 내보내게 하려 했는데, 결과가 잘 작동해도 404 에러를 내보내길래 그냥 에러를 안내보내게 바꿨다, 동작은 아주 잘 됩니다)
      그래서 검사를 해서 조건에 맞는 것을 확인을 하면 확정으로 바꿔주고 판매 게시글에도 상태를 판매완료로 바꿔준다.
      그리고 findAllBySalesItemIdAndIdNot과 List를 이용하여 확정되지 않은 다른 구매 제안의 상태는 모두 거절이 되도록 바꿔준다.
      "구매가 확정되었습니다"를 응답해준다. 

* 4  (deleteNegotiation)
    * PathVariable로 받은 itemId, proposalId, RequestBody로 받은 NegotiationDeleteDto dto(네고 작성자, password 등의 정보)를 
      쿼리메소드에 다음과 같이 넣어주어 negotiationRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId, Math.toIntExact(id), writer, password) 일치하는게 있으면 삭제하도록 하였다.
      
