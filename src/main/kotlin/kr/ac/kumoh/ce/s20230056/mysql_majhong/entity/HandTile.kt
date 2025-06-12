package kr.ac.kumoh.ce.s20230056.mysql_majhong.entity

import jakarta.persistence.*

@Entity
@Table(name = "hand_tile")
data class HandTile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val groupIndex: Int,

    @Column(nullable = false)
    val isFromFuro: Boolean,

    @Column(nullable = false)
    val isWinningTile: Boolean,

    @ManyToOne
    @JoinColumn(name = "hand_id", nullable = false)
    val hand: Hand,

    @ManyToOne
    @JoinColumn(name = "tile_id", nullable = false)
    val tile: Tile
)
