package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
// JPA 의 프록시 기술을 사용하기 위해 좀 열어놔야 함.
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 연관관계 필드 (여기서는 team) 은 toString 하지 마라. (무한 루프)
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") // team_id 를 FK 값으로 설정, 매핑의 주인
    private Team team;

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        // 해당 Member 객체에 Team 이 있을 떄만 changeTeam() 메서드 실행하도록 설정
        if (team != null) {
            changeTeam(team);
        }
    }

    // 양방향 매핑에서 다(Member) 객체는, 일(Team) 을 변경할 수 있다.
    // Member 의 소속팀이 바껴야 하기 때문에 team 을 갈아끼워주고, 양방향 매핑이기 때문에 바뀔 팀 에서도 멤버를 추가한다.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
