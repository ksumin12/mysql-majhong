package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "yaku_rule")
data class YakuRule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val pan: Int,

    @Column(nullable = false)
    val isMenzen: Boolean
)
