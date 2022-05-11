package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

// 해당 어노테이션이 있어야 Member 엔티티가 생성될 때 아래 설정한 @Column 속성들이 엔티티에 등록된다.
// 없으면 createdDate, updatedDate 속성이 Member 엔티티에 등록되지 않는다.
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false) // 수정 X
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // Persist 전에 호출 (영속성 컨텍스트 추가 전에 호출)
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        // 등록일, 수정일을 함께 셋팅해놔야 나중에 쿼리 날릴 때 편하다 (아직 잘 모르겠음)
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate // Update 전에 호출
    private void preUpdate() {

        // Update 쿼리 날아갈 때 현재 시간을 수정일로 맞춰준다.
        this.updatedDate = LocalDateTime.now();
    }

}
