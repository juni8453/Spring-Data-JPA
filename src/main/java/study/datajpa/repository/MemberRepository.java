package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 쿼리 메서드 사용
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 엔티티에 작성한 NamedQuery 사용
    // @Param 을 사용해 정확하게 username 을 작성해줘야 엔티티 NamedQuery 의 username 과 매핑된다.
    // 사실 JpaRepository<Member, Long> 에서 Member 타입에 dot 을 하고 (Member. ~) NamedQuery 를 찾는다.
    // 따라서 19줄의 코드는 없어도 무방하고, 만약 NamedQuery 가 없다면, 쿼리 메서드를 찾아 실행하는 우선순위를 가진다.
    // 근데 실무에서는 NamedQuery 는 잘 안쓴다.
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // Repository 인터페이스에 쿼리 바로 정의하기
    // 실무에서 가장 많이 사용하는 방법
    @Query("Select m From Member As m Where m.username = :username And m.age = :age")
    List<Member> findUser(
            @Param("username") String username,
            @Param("age") int age);

    // ----------------------------------------

    // String 타입 조회하기
    @Query("Select m.username From Member As m")
    List<String> findUserNameList();

    // DTO 타입 조회하기
    // new 연산자를 사용해 마치 생성자를 호출하듯 인자들을 넘겨준다.
    @Query("Select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) From Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션을 In 절로 묶어서 조회하기
    // Member Entity 에서 names 리스트의 String 타입 인자들을 In 절로 묶어서 조회한다.
    // List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB")); 이런 식으로 사용
    @Query("select m from Member As m Where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    // ----------------------------------------

    // 두 번째 인자로 Pageable 인터페이스를 넘겨주고 PageRequest 클래스로 구현하면 된다.
    // 검색 조건 : 나이가 10살인 Member 객체

    // TODO totalCount 를 가져오는 것은 아무리 최적화가 잘 되어있다고 한들 굉장히 오래 걸리는 작업이다.
    //  만약 Member 와 Team 을 left join 한다고 가정하면, totalCount 를 구하는데 굳이 join 할 필요가 없다. 어짜피 결과는 똑같으니까
    //  따라서 countQuery 를 날릴 떄는 join 절이 없어도 되니까 join 절 없이 수동으로 짜주자. (성능 최적화)
    //  데이터가 많지 않다면 나눌 필요는 없고 성능이 안나오면 그때 고려하면 된다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // Slice, List 는 쿼리 메서드 형태로 만들어줘야 동작한다. (메서드 이름에 이상있으면 동작 X)
//    Slice<Member> findByAge(int age, Pageable pageable);
//
//    List<Member> findByAge(int age, Pageable pageable);
}
