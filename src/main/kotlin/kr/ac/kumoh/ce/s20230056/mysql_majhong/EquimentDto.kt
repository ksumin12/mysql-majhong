package kr.ac.kumoh.ce.s20230056.mysql_majhong
import java.sql.Timestamp

// 핸드 상세 정보 DTO (메인 조회용)
data class EquimentDto(
    val handId: Long,                      // DB의 AUTO_INCREMENT는 Long
    val isTsumo: Boolean,
    val playerId: Long,                    // FK도 Long
    val round: String,
    val isReach: Boolean,
    val isDealer: Boolean,
    val playerCount: Int,          // Enum 타입 유지
    val northDoraCount: Int,
    val playerWind: Wind?,                 // String? → Wind? 수정
    val roundWind: Wind,                   // String → Wind 수정
    val honbaCount: Int,
    val doraCount: Int,
    val uraDoraCount: Int,
    val redDoraCount: Int,
    val yakuName: String?,
    val yakuPan: Int?,
    val fuValue: Int?,
    val totalFu: Int?,
    val totalPan: Int?,
    val basePoint: Int?,
    val dealerTsumoPoint: Int?,
    val dealerRonPoint: Int?,
    val nonDealerTsumoPoint: Int?,
    val nonDealerRonPoint: Int?,
    val honbaBonus: Int?,
    val totalPoint: Int?,
    val isYakuman: Boolean?,
    val yakumanCount: Int?,
    val doraTotalCount: Int?,
    val createdAt: Timestamp
)
 {
     fun getPlayerCountDisplay(): String = "${playerCount}인전"


     // null 안전 확장 함수들을 companion object로 이동
    companion object {

        fun EquimentDto.hasValidScore(): Boolean = totalPoint?.let { it > 0 } ?: false

        fun EquimentDto.getScoreOrDefault(): Int = totalPoint ?: 0

        fun EquimentDto.isYakumanSafe(): Boolean = isYakuman ?: false

        fun EquimentDto.getPlayerWindDisplay(): String  =
            playerWind?.name ?: "미설정"

        fun EquimentDto.getRoundDisplay(): String =
            round.takeIf { it.isNotBlank() } ?: "알 수 없음"

        fun EquimentDto.getDisplayName(): String =
            "핸드 #$handId (${if (isDealer) "오야" else "자"}, ${if (isTsumo) "쯔모" else "론"})"

        fun EquimentDto.getScoreDisplay(): String =
            totalPoint?.let { "${it}점" } ?: "점수 미계산"

        fun EquimentDto.getYakuDisplay(): String =
            yakuName?.let { "$it (${yakuPan ?: 0}판)" } ?: "역 없음"
    }
}

fun EquimentDto.hasScore(): Boolean = totalPoint?.let { it > 0 } ?: false


// 패 정보 DTO
data class TileDto(
    val type: String, // 패 종류 (만수, 통수, 삭수, 자패)
    val number: Int, // 패 번호
    val isRedDora: Boolean // 적도라 여부
) {
    fun isValid(): Boolean {
        return type.isNotBlank() &&
                number > 0 &&
                when(type) {
                    "만수", "통수", "삭수" -> number in 1..9
                    "자패" -> number in 1..7
                    else -> false
                }
    }

    fun getDisplayName(): String =
        "${type}${number}${if (isRedDora) "(적)" else ""}"
}

// 손패 입력을 위한 DTO (Use Case 3.2)
data class HandTileDto(
    val groupIndex: Int, // 패 그룹 인덱스 (0=머리, 1-4=면자)
    val isFromFuro: Boolean, // 후로(퐁/치/깡) 여부
    val isWinningTile: Boolean, // 화료패 여부
    val tile: TileDto? // Left outer join으로 인해 nullable
) {
    fun isValid(): Boolean = tile?.isValid() ?: false

    fun getTileDisplay(): String = tile?.getDisplayName() ?: "패 없음"
}

//점수 조회용
data class ScoreDto(
    val id: Long,
    val handId: Long,
    val totalFu: Int,
    val totalPan: Int,
    val basePoint: Int,
    val totalPoint: Int,
    val isYakuman: Boolean,
    val yakumanCount: Int
)

// 역 정보 DTO
data class YakuDto(
    val id: Int? = null,
    val name: String?, // Left outer join으로 인해 nullable
    val pan: Int?, // Left outer join으로 인해 nullable
    val isMenzen: Boolean?, // Left outer join으로 인해 nullable
    val isYakuman: Boolean?, // Left outer join으로 인해 nullable
    val description: String? // 원래부터 nullable
) {
    fun isValid(): Boolean =
        !name.isNullOrBlank() && (pan ?: 0) > 0

    fun getDisplayName(): String =
        name?.let { "$it (${pan ?: 0}판)" } ?: "역 없음"

    fun isMenzenSafe(): Boolean = isMenzen ?: false

    fun isYakumanSafe(): Boolean = isYakuman ?: false
}

// 부 정보 DTO
data class FuDto(
    val id: Int? = null,
    val name: String?, // Left outer join으로 인해 nullable
    val description: String?, // Left outer join으로 인해 nullable
    val fuValue: Int?, // Left outer join으로 인해 nullable
    val category: String?, // Left outer join으로 인해 nullable
    val count: Int? = 1 // Left outer join으로 인해 nullable
) {
    fun isValid(): Boolean =
        !name.isNullOrBlank() && (fuValue ?: 0) > 0 && (count ?: 0) > 0

    fun getTotalFu(): Int = (fuValue ?: 0) * (count ?: 1)

    fun getDisplayName(): String =
        name?.let { "$it (${fuValue ?: 0}부 x ${count ?: 1})" } ?: "부 없음"
}

// Use Case 3.4 점수 계산 결과 DTO
data class ScoreResultDto(
    val totalPoint: Int?, // 계산되지 않았을 수 있으므로 nullable
    val dealerTsumoPoint: Int?, // 오야 쯔모 점수
    val dealerRonPoint: Int?, // 오야 론 점수
    val nonDealerTsumoPoint: Int?, // 자 쯔모 점수
    val nonDealerRonPoint: Int?, // 자 론 점수
    val honbaBonus: Int?, // 본장 보너스
    val isYakuman: Boolean?, // 역만 여부
    val yakumanCount: Int? // 역만 개수
) {
    fun getTotalPointSafe(): Int = totalPoint ?: 0

    fun getDealerTsumoPointSafe(): Int = dealerTsumoPoint ?: 0

    fun getDealerRonPointSafe(): Int = dealerRonPoint ?: 0

    fun getNonDealerTsumoPointSafe(): Int = nonDealerTsumoPoint ?: 0

    fun getNonDealerRonPointSafe(): Int = nonDealerRonPoint ?: 0

    fun getHonbaBonusSafe(): Int = honbaBonus ?: 0

    fun isYakumanSafe(): Boolean = isYakuman ?: false

    fun getYakumanCountSafe(): Int = yakumanCount ?: 0
}

// 부 요청 DTO
data class FuRequestDto(
    val fuRuleId: Int,
    val count: Int = 1
) {
    fun isValid(): Boolean = fuRuleId > 0 && count > 0
}

// 핸드 생성/수정을 위한 요청 DTO
data class HandCreateRequestDto(
    val playerId: Int,
    val playerCount: String = "FOUR", // "THREE" 또는 "FOUR"
    val northDoraCount: Int = 0,
    val isDealer: Boolean = false,
    val playerWind: String? = null, // "EAST", "SOUTH", "WEST", "NORTH"
    val roundWind: String = "EAST",
    val isTsumo: Boolean,
    val round: String, // "동1국", "동2국" 등
    val honbaCount: Int = 0,
    val isReach: Boolean = false,
    val doraCount: Int = 0,
    val uraDoraCount: Int = 0,
    val redDoraCount: Int = 0,
    val tiles: List<HandTileDto> = emptyList(), // null 대신 빈 리스트
    val yakus: List<Int> = emptyList(), // YakuRule ID 목록
    val fus: List<FuRequestDto> = emptyList()
) {
    fun isValid(): Boolean {
        return playerId > 0 &&
                round.isNotBlank() &&
                playerCount in listOf("THREE", "FOUR") &&
                northDoraCount in 0..4 &&
                honbaCount >= 0 &&
                doraCount >= 0 &&
                uraDoraCount >= 0 &&
                redDoraCount >= 0 &&
                tiles.all { it.isValid() } && // 안전한 컬렉션 검증
                yakus.all { it > 0 } &&
                fus.all { it.isValid() }
    }

    fun getPlayerWindSafe(): String = playerWind ?: "EAST"

    fun getTotalDoraCount(): Int = doraCount + uraDoraCount + redDoraCount
}

// 핸드 수정을 위한 요청 DTO
data class HandUpdateRequestDto(
    val handId: Int,
    val playerId: Int? = null,
    val playerCount: String? = null,
    val northDoraCount: Int? = null,
    val isDealer: Boolean? = null,
    val playerWind: String? = null,
    val roundWind: String? = null,
    val isTsumo: Boolean? = null,
    val round: String? = null,
    val honbaCount: Int? = null,
    val isReach: Boolean? = null,
    val doraCount: Int? = null,
    val uraDoraCount: Int? = null,
    val redDoraCount: Int? = null,
    val tiles: List<HandTileDto>? = null,
    val yakus: List<Int>? = null,
    val fus: List<FuRequestDto>? = null
) {
    fun isValid(): Boolean {
        return handId > 0 &&
                (playerId?.let { it > 0 } ?: true) &&
                (playerCount?.let { it in listOf("THREE", "FOUR") } ?: true) &&
                (northDoraCount?.let { it in 0..4 } ?: true) &&
                (honbaCount?.let { it >= 0 } ?: true) &&
                (doraCount?.let { it >= 0 } ?: true) &&
                (uraDoraCount?.let { it >= 0 } ?: true) &&
                (redDoraCount?.let { it >= 0 } ?: true) &&
                (round?.let { it.isNotBlank() } ?: true) &&
                (tiles?.all { it.isValid() } ?: true) &&
                (yakus?.all { it > 0 } ?: true) &&
                (fus?.all { it.isValid() } ?: true)
    }
}

// 점수 계산 요청 DTO
data class ScoreCalculationRequestDto(
    val handId: Int
) {
    fun isValid(): Boolean = handId > 0
}

// 점수 계산 응답 DTO
data class ScoreCalculationResponseDto(
    val handId: Int,
    val success: Boolean,
    val message: String? = null,
    val score: ScoreResultDto? = null
) {
    fun getMessageSafe(): String = message ?: if (success) "성공" else "실패"

    fun hasScore(): Boolean = score != null
}

// 핸드 목록 조회를 위한 간단한 DTO
data class HandSummaryDto(
    val handId: Int,
    val playerId: Int,
    val round: String,
    val isDealer: Boolean,
    val isTsumo: Boolean,
    val isReach: Boolean,
    val totalPoint: Int?,
    val isYakuman: Boolean?,
    val createdAt: String
) {
    fun getTotalPointSafe(): Int = totalPoint ?: 0

    fun isYakumanSafe(): Boolean = isYakuman ?: false

    fun getDisplayName(): String =
        "핸드 #$handId (${if (isDealer) "오야" else "자"}, ${if (isTsumo) "쯔모" else "론"})"
}

// 통계 정보 DTO
data class HandStatisticsDto(
    val totalHands: Long,
    val yakumanHands: Long,
    val reachHands: Long,
    val dealerHands: Long,
    val tsumoHands: Long,
    val averageScore: Double?,
    val highestScore: Int?,
    val lowestScore: Int?
) {
    fun getAverageScoreSafe(): Double = averageScore ?: 0.0

    fun getHighestScoreSafe(): Int = highestScore ?: 0

    fun getLowestScoreSafe(): Int = lowestScore ?: 0

    fun getYakumanRate(): Double =
        if (totalHands > 0) (yakumanHands.toDouble() / totalHands * 100) else 0.0

    fun getReachRate(): Double =
        if (totalHands > 0) (reachHands.toDouble() / totalHands * 100) else 0.0
}

// 역 통계 DTO
data class YakuStatisticsDto(
    val yakuName: String,
    val usageCount: Long,
    val percentage: Double
) {
    fun getDisplayName(): String = "$yakuName (${usageCount}회, ${String.format("%.1f", percentage)}%)"
}

// 에러 응답 DTO
data class ErrorResponseDto(
    val success: Boolean = false,
    val message: String,
    val errorCode: String? = null,
    val timestamp: String = java.time.LocalDateTime.now().toString()
) {
    fun getErrorCodeSafe(): String = errorCode ?: "UNKNOWN_ERROR"
}

// 성공 응답 DTO
data class SuccessResponseDto<T>(
    val success: Boolean = true,
    val message: String = "Success",
    val data: T? = null,
    val timestamp: String = java.time.LocalDateTime.now().toString()
) {
    fun hasData(): Boolean = data != null

    fun getDataSafe(): T? = data
}

// 페이징 정보 DTO
data class PageInfoDto(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    fun isValidPage(): Boolean = page >= 0 && size > 0 && totalElements >= 0
}

// 페이징된 핸드 목록 응답 DTO
data class PagedHandResponseDto(
    val hands: List<HandSummaryDto>,
    val pageInfo: PageInfoDto
) {
    fun isEmpty(): Boolean = hands.isEmpty()

    fun getHandCount(): Int = hands.size
}

// 컬렉션 null 안전 확장 함수들
fun List<EquimentDto?>.filterValidHands(): List<EquimentDto> =
    this.filterNotNull().filter { it.handId > 0 }

fun List<EquimentDto>.calculateAverageScore(): Double? =
    this.mapNotNull { it.totalPoint }.takeIf { it.isNotEmpty() }?.average()

fun List<HandSummaryDto?>.filterValidSummaries(): List<HandSummaryDto> =
    this.filterNotNull().filter { it.handId > 0 }

fun List<YakuDto?>.filterValidYakus(): List<YakuDto> =
    this.filterNotNull().filter { it.isValid() }

fun List<FuDto?>.filterValidFus(): List<FuDto> =
    this.filterNotNull().filter { it.isValid() }

// 안전한 변환 함수들
fun String?.toIntSafe(): Int? = this?.toIntOrNull()

fun String?.toBooleanSafe(): Boolean = this?.toBooleanStrictOrNull() ?: false

fun Any?.toStringSafe(): String = this?.toString() ?: ""
