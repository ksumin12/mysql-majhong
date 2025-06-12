package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "hand")
data class Hand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val isTsumo: Boolean,

    val playerId: Int? = null,

    val round: String? = null,

    @Column(nullable = false)
    val isReach: Boolean,

    @Column(nullable = false)
    val isDealer: Boolean
)
