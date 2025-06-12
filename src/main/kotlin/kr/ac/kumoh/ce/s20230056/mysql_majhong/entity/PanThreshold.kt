package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "pan_threshold_table", uniqueConstraints = [UniqueConstraint(columnNames = ["fu", "pan"])])
data class PanThreshold(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val fu: Int,

    @Column(nullable = false)
    val pan: Int,

    @Column(nullable = false)
    val dealerTsumoPoint: Int,

    @Column(nullable = false)
    val dealerRonPoint: Int,

    @Column(nullable = false)
    val nonDealerTsumoPoint: Int,

    @Column(nullable = false)
    val nonDealerRonPoint: Int
)
