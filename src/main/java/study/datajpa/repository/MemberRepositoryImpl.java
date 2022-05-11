package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
// 구현체의 클래스 이름은 인터페이스가 상속되고 있는 인터페이스 이름 + Impl 로 붙여줘야 한다.
// 여기서는 MemberRepository 인터페이스에 MemberRepositoryCustom 인터페이스가 상속되고 있으므로,
// MemberRepository + Impl 로 구현체 이름을 지정해야 한다.
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member As m")
                .getResultList();
    }
}
