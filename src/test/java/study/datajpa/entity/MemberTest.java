package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void MemberEntityTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        // given 2명의 멤버 객체가 주어진다.
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        em.persist(member1);
        em.persist(member2);

        // DB 에 날아가는 쿼리를 확실하게 보기 위해 강제로 flush() 호출 후 clear()
        em.flush();
        em.clear();

        List<Member> resultMembers = em.createQuery("Select m From Member As m", Member.class)
                .getResultList();


        for (Member member : resultMembers) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }
}