package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "tile", uniqueConstraints = [UniqueConstraint(columnNames = ["type", "number", "isRedDora"])])
data class Tile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val number: Int,

    @Column(nullable = false)
    val isRedDora: Boolean
)
