package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member memberA = new Member("memberA", 10, null);
        Member savedMember = memberRepository.save(memberA);

        // 있을 수도 있고 없을 수도 있기 때문에 JPA 에서는 기본적으로 Optional 로 제공한다.
        // Test 니까 그냥 get() 으로 꺼내 쓴다.
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(memberA.getId());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
        assertThat(findMember).isEqualTo(memberA);
    }

    // memberRepository 는 Interface 이지만, 놀랍게도 동작한다.
    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("member2", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // count() 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();

        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // findByUsername() 메서드는 Repository 인터페이스를 거쳐 Entity 의 @NamedQuery 를 찾아 실행한다.
        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testStringQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> userNameList = memberRepository.findUserNameList();
        String username = userNameList.get(0);
        assertThat(username).isEqualTo("AAA");
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 컬렉션 조회
        // TODO 중요) JPA 에서 컬렉션 타입 조회할 떄, 무조건 null 이 아닌 빈 값이라도 나오게 보장해준다.
        //  즉, AAA 가 아닌 이상한 이름을 조회해도 size 를 찍어봤을 떄 0 이 나오도록 보장된다는 것. (매우 중요중요중요)
        List<Member> findListMembers = memberRepository.findListByUsername("AAA");

        // 단건 조회
        // 컬렉션 조회와 달리 값이 없다면 null 이 반환된다. (자기가 알아서 try - catch 해서 빈 값일 때 null 이 나오도록 구현되어 있다.)
        // 단건 조회인데, 값이 없는게 아닌 중복 값이 있다면 즉, 단건이 아니라면 NonUniqueResultException 예외가 발생한다.
        Member findMember = memberRepository.findMemberByUsername("AAA");

        // Optional 조회
        // 위의 단건 조회의 고민은 자바 8 이전. 즉, Optional 등장 이후 저런 고민은 필요가 없게 되었다.
        Optional<Member> findMemberByOptional = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void SpringDataJpaPaging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // Spring Data JPA 는 페이지 인자가 0부터 시작한다 (1 부터 시작 아님)
        // TODO 검색조건 : 나이가 10살인 Member 객체
        // 0 페이지 부터 3개 씩, username 을 기준으로 내림차순 정렬
        int age = 10;

        // Pageable 인터페이스를 PageRequest 클래스로 구현한다.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // TODO 반환타입이 Page 라면 totalCount 쿼리가 같이 날아간다. 따라서 totalCount 를 따로 구현할 필요가 없다.
        // 이 반환타입에 따라서 totalCount 쿼리를 날릴지 안 날랄지 결정된다.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // TODO 실무 꿀팁 (Entity -> DTO 로 변환)
        Page<MemberDto> memberDtoPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // TODO 반환타입이 Slice 라면 totalCount 쿼리를 날리지 않는다.
        //  PageRequest 에서 page 0, size 3 으로 구현했는데, 눈속임용으로 size 3 에 1을 더한 4개의 데이터를 가지고 온다.
//        Slice<Member> slice = memberRepository.findByAgeSlice(age, pageRequest);

        // TODO List 형태로도 반환받을 수 있다. 여기서도 따로 totalCount 쿼리는 날아가지 않는다.
        //  List<> 반환타입은 Page<>, Slice<> 타입의 메서드를 사용할 수 없다.
        //  데이터만 10개 끊어서 가져오고 싶고 다른 페이지가 있는지 없는지, 몇 페이지인지 이런거는 상관없을 때 쓰면 유용하다.
//        List<Member> list = memberRepository.findByAgeList(age, pageRequest);

        // 현재 페이지의 데이터가 담기는 List
        List<Member> content = page.getContent();
//        List<Member> sliceContent = slice.getContent();

        // 모든 데이터의 totalCount
        long totalElements = page.getTotalElements();

        // 현재 페이지가 몇 페이지인지 ?
        int pageNumber = page.getNumber();
//        int sliceNumber = slice.getNumber();

        // 전체 페이지가 몇 페이지인지 ?
        int totalPageCount = page.getTotalPages();

        // 현재 페이지가 첫 페이지인지 ?
        boolean first = page.isFirst();
//        boolean sliceFirst = slice.isFirst();

        // 다음 페이지가 있는지 ?
        boolean hasNextPage = page.hasNext();
//        boolean hasNextSlice = slice.hasNext();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(pageNumber).isEqualTo(0);
        assertThat(totalPageCount).isEqualTo(2);
        assertThat(first).isTrue();
        assertThat(hasNextPage).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // JpaRepository 의 save() 내부의 em.persist() 에 의해 영속성 컨텍스트에 추가된다.
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // 벌크성 연산은 영속성 컨텍스트를 무시하고 바로 DB 에 값을 때려넣는다.
        // 따라서 벌크 연산으로 DB 에 값이 들어간 이후 영속성 컨텍스트와 DB 값이 맞지 않을 수 있기 때문에
        // flush() 를 통해 영속성 컨텍스트에 쌓여있는 객체들을 DB 에 보내고, clear() 를 통해 영속성 컨텍스트를 지워줘야한다.
        // 영속성 컨텍스트 내부에 값이 없어야 다시 조회하거나 할 때 DB 값을 조회하기 때문 (영속성 컨텍스트 값이 있다면 DB 까지 안가니까)
        // @Modifying(clearAutomatically = true) 옵션을 통해 자동으로 설정할 수 있다.
        // em.flush();
        // em.clear();

        //then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 영속성 컨텍스트 된 객체들을 깔끔하게 비워준다.
        em.clear();

        //when
        // Select 로 Member 객체만 가져오는 쿼리 가져옴
        // findAll() 은 fetch join 이 아니다.
//        List<Member> members = memberRepository.findAll();

        // fetch 조인 형식으로 Member 객체에 연관된 Team 을 한 번에 가져올 수 있다.
        // TODO 참고) JPQL 은 영속성 컨텍스트에서 엔티티를 먼저 찾지 않고 항상 DB 에 SQL 을 실행해 결과를 조회한다.
        //  1. JPQL 을 호출 -> DB 에 우선적으로 조회
        //  2. 조회한 값을 영속성 컨텍스트에 저장 (flush() 발생)
        //  3. 영속성 컨텍스트에 조회할 때 이미 존재하는 데이터가 있다면 데이터를 버린다.
        //  즉, JPQL 입장에서 즉시, 지연 로딩과 같은 패치 전략을 무시하고 JPQL 만 사용해 SQL 을 생성한다.
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        // flush() 를 호출해 영속성 컨텍스트의 값들을 DB 애 보내준다 (동기화)
        em.flush();
        em.clear();

        // Member findMember = memberRepository.findById(member.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");

        // 이제 update 쿼리 안나감 (readOnly 이기 때문)
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

}