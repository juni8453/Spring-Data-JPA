package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    // JPA 의 프록시 기술을 사용하기 위해 좀 열어놔야 함.
    protected Member() {}

    public Member(String username) {
        this.username = username;
    }
}
