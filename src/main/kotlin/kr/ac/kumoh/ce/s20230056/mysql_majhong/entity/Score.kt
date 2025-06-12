package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "score")
data class Score(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val totalPoint: Int,

    @Column(nullable = false)
    val ronPointFromDealer: Int,

    @Column(nullable = false)
    val ronPointFromNonDealer: Int,

    @Column(nullable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @OneToOne
    @JoinColumn(name = "threshold_id", nullable = false)
    val threshold: PanThreshold,

    @OneToOne
    @JoinColumn(name = "hand_id", nullable = false, unique = true)
    val hand: Hand
)
