package study.datajpa.dto;

import lombok.Data;

// DTO 라 그냥 @Data 사ㅛㅇㅇ
@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
