package kr.ac.kumoh.ce.s20230056.mysql_majhong

import jakarta.persistence.*
import java.sql.Timestamp
import com.fasterxml.jackson.annotation.*

@Entity
@Table(
    name = "tile",
    uniqueConstraints = [UniqueConstraint(columnNames = ["type", "number", "is_red_dora"])]
)
data class Tile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, length = 20)
    val type: String,

    @Column(nullable = false)
    val number: Int,

    @Column(name = "is_red_dora", nullable = false)
    val isRedDora: Boolean = false
)

@Entity
data class Hand(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // 3.1.1 게임 인원수 설정
    @Column(name = "player_count", nullable = false)
    val playerCount: Int = 4,  // @Enumerated 제거

    // 3.1.1.1 북빼기 설정 (3인 마작용)
    @Column(name = "north_dora_count", nullable = false)
    val northDoraCount: Int = 0,

    // 3.1.2 오야 여부 설정
    @Column(name = "is_dealer", nullable = false)
    val isDealer: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(name = "player_wind")
    val playerWind: Wind? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "round_wind", nullable = false)
    val roundWind: Wind = Wind.동,

    // 3.1.3 화료한 방식 설정
    @Column(name = "is_tsumo", nullable = false)
    val isTsumo: Boolean,

    // 3.1.4 판 정보 등록
    @Column(nullable = false, length = 10)
    val round: String,

    @Column(name = "honba_count", nullable = false)
    val honbaCount: Int = 0,

    // 3.3 리치 여부 선택
    @Column(name = "is_reach", nullable = false)
    val isReach: Boolean = false,

    // 도라 관련
    @Column(name = "dora_count", nullable = false)
    val doraCount: Int = 0,

    @Column(name = "ura_dora_count", nullable = false)
    val uraDoraCount: Int = 0,

    @Column(name = "red_dora_count", nullable = false)
    val redDoraCount: Int = 0,

    // 기타
    @Column(name = "player_id", nullable = false)
    val playerId: Int,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    // JPQL을 위한 관계 매핑
    @OneToMany(mappedBy = "hand", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val handTiles: List<HandTile> = emptyList(),

    @OneToMany(mappedBy = "hand", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val handYakus: List<HandYaku> = emptyList(),

    @OneToMany(mappedBy = "hand", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val handFus: List<HandFu> = emptyList(),

    @OneToOne(mappedBy = "hand", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val score: Score? = null
)




enum class PlayerCount(val count: Int) {
    THREE(3),
    FOUR(4);

    companion object {
        fun fromCount(count: Int): PlayerCount {
            return when (count) {
                3 -> THREE
                4 -> FOUR
                else -> FOUR // 기본값
            }
        }
    }
}

enum class Wind {
    동, 남, 서, 북  // 한글 그대로 사용
}



@Entity
@Table(name = "hand_tile")
data class HandTile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "group_index", nullable = false)
    val groupIndex: Int,

    @Column(name = "is_from_furo", nullable = false)
    val isFromFuro: Boolean = false,

    @Column(name = "is_winning_tile", nullable = false)
    val isWinningTile: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hand_id", nullable = false)
    val hand: Hand,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tile_id", nullable = false)
    val tile: Tile
)

@Entity
@Table(name = "yaku_rule")
data class YakuRule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(length = 30, unique = true, nullable = false)
    val name: String,

    @Column(nullable = false)
    val pan: Int,

    @Column(name = "is_menzen", nullable = false)
    val isMenzen: Boolean,

    @Column(name = "is_yakuman", nullable = false)
    val isYakuman: Boolean = false,

    @Column(length = 100)
    val description: String? = null,

    // JPQL을 위한 관계 매핑
    @OneToMany(mappedBy = "yakuRule", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val handYakus: List<HandYaku> = emptyList()
)

@Entity
@Table(name = "hand_yaku")
data class HandYaku(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hand_id", nullable = false)
    val hand: Hand,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yaku_rule_id", nullable = false)
    val yakuRule: YakuRule
)

@Entity
@Table(name = "fu_rule")
data class FuRule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(length = 50, nullable = false)
    val name: String,

    @Column(length = 100, nullable = false)
    val description: String,

    @Column(name = "fu_value", nullable = false)
    val fuValue: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: FuCategory,

    @Column(name = "is_menzen_only", nullable = false)
    val isMenzenOnly: Boolean = false,

    // JPQL을 위한 관계 매핑
    @OneToMany(mappedBy = "fuRule", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val handFus: List<HandFu> = emptyList()
)

enum class FuCategory {
    BASE,
    TRIPLET,
    PAIR,
    SPECIAL,
    KAN
}

@Entity
@Table(name = "hand_fu")
data class HandFu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hand_id", nullable = false)
    val hand: Hand,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fu_rule_id", nullable = false)
    val fuRule: FuRule,

    @Column(nullable = false)
    val count: Int = 1
)

@Entity
@Table(
    name = "pan_threshold_table",
    uniqueConstraints = [UniqueConstraint(columnNames = ["fu", "pan"])]
)
data class PanThresholdTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val fu: Int,

    @Column(nullable = false)
    val pan: Int,

    @Column(name = "dealer_tsumo_point", nullable = false)
    val dealerTsumoPoint: Int,

    @Column(name = "dealer_ron_point", nullable = false)
    val dealerRonPoint: Int,

    @Column(name = "non_dealer_tsumo_point", nullable = false)
    val nonDealerTsumoPoint: Int,

    @Column(name = "non_dealer_ron_point", nullable = false)
    val nonDealerRonPoint: Int,

    // JPQL을 위한 관계 매핑
    @OneToMany(mappedBy = "threshold", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val scores: List<Score> = emptyList()
)

@Entity
data class Score(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    // 계산 기초 정보
    @Column(name = "total_fu", nullable = false)
    val totalFu: Int,

    @Column(name = "total_pan", nullable = false)
    val totalPan: Int,

    @Column(name = "base_point", nullable = false)
    val basePoint: Int,

    // 모든 경우의 점수
    @Column(name = "dealer_tsumo_point", nullable = false)
    val dealerTsumoPoint: Int,

    @Column(name = "dealer_ron_point", nullable = false)
    val dealerRonPoint: Int,

    @Column(name = "non_dealer_tsumo_point", nullable = false)
    val nonDealerTsumoPoint: Int,

    @Column(name = "non_dealer_ron_point", nullable = false)
    val nonDealerRonPoint: Int,

    // 본장 추가 점수
    @Column(name = "honba_bonus", nullable = false)
    val honbaBonus: Int = 0,

    @Column(name = "total_point", nullable = false)
    val totalPoint: Int,

    // 추가 정보
    @Column(name = "is_yakuman", nullable = false)
    val isYakuman: Boolean = false,

    @Column(name = "yakuman_count", nullable = false)
    val yakumanCount: Int = 0,

    @Column(name = "dora_total_count", nullable = false)
    val doraTotalCount: Int = 0,

    // 메타데이터
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    // 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threshold_id")
    val threshold: PanThresholdTable? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hand_id", unique = true, nullable = false)
    val hand: Hand
)