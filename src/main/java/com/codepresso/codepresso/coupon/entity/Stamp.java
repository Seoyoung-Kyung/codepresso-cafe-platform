package com.codepresso.codepresso.coupon.entity;

import com.codepresso.codepresso.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;




@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "stamp")
@Entity
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_id")
    private Long id;

    // FK → Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "earned_date")
    private LocalDateTime earnedDate;

    @Column(name = "quantity")
    private Integer quantity;
}

