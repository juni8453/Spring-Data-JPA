package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false) 요거 있어야 insert 보임
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("Member 를 저장하면 저장을 시도한 Member 와 Repository 에 저장된 Member 가 일치해야 한다.")
    void saveAndFindTest() {
        Member memberA = new Member("memberA", 10, null);

        Member saveMember = memberJpaRepository.save(memberA);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        // 사실 같은 트랜잭션 이라서 영속성 컨텍스트에 저장된 ID 값으로 관리되기 때문에 무조건 true 이다.
        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
    }
}