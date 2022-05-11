package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // Optional 사용 X
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // Optional 을 사용해서 멤버 반환
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return em.createQuery("Select m from Member As m", Member.class)
                .getResultList();
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    // 페이징 처리 (같은 age 를 갖는 Member 객체 데이터를 username 기준 내림차순으로 정렬)
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member As m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 현재 페이지에 몇 개의 데이터가 있는지 ?
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member As m where m.age =: age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    // 벌크성 수정 쿼리
    // Parameter age 보다 큰 age 를 모두 + 1
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}