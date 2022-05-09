package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member memberA = new Member("memberA");
        Member savedMember = memberRepository.save(memberA);

        // 있을 수도 있고 없을 수도 있기 때문에 JPA 에서는 기본적으로 Optional 로 제공한다.
        // Test 니까 그냥 get() 으로 꺼내 쓴다.
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(memberA.getId());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
        assertThat(findMember).isEqualTo(memberA);
    }
}