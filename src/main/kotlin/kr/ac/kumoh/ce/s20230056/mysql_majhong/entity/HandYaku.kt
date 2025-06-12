package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "hand_yaku")
data class HandYaku(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "hand_id", nullable = false)
    val hand: Hand,

    @ManyToOne
    @JoinColumn(name = "yaku_rule_id", nullable = false)
    val yakuRule: YakuRule
)
