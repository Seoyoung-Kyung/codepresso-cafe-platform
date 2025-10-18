package com.codepresso.codepresso.entity.board;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "board_type")
@Entity
public class BoardType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_type_id")
    private Long id;

    @Column(name = "board_type_name", length = 100)
    private String boardTypeName;

    @Column(name = "board_type_description", length = 500)
    private String boardTypeDescription;

    // 게시판 유형 ↔ 게시판 (1:N)
    @OneToMany(mappedBy = "boardType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Board> boards = new java.util.ArrayList<>();
}

