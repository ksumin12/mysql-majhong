package kr.ac.kumoh.ce.s20230056.mysql_majhong

import org.springframework.stereotype.Service
import kotlin.math.*


@Service
class HandService(
    private val handRepository: HandRepository,
    private val scoreRepository: ScoreRepository
) {
    // getHandDetails 메서드 추가
    fun getHandDetails(): List<EquimentDto> {
        return handRepository.getHandDetails()
    }

    fun calculateScoreOnly(handId: Int): ScoreCalculationResponseDto {
        val hand = handRepository.findById(handId).orElse(null)
            ?: return ScoreCalculationResponseDto(
                handId = handId,
                success = false,
                message = "핸드를 찾을 수 없습니다."
            )

        // 실제 Hand 데이터를 기반으로 점수 계산
        val calculatedScore = calculateActualScore(hand)

        val scoreResult = ScoreResultDto(
            totalPoint = calculatedScore.totalPoint,
            dealerTsumoPoint = calculatedScore.dealerTsumoPoint,
            dealerRonPoint = calculatedScore.dealerRonPoint,
            nonDealerTsumoPoint = calculatedScore.nonDealerTsumoPoint,
            nonDealerRonPoint = calculatedScore.nonDealerRonPoint,
            honbaBonus = calculatedScore.honbaBonus,
            isYakuman = calculatedScore.isYakuman,
            yakumanCount = calculatedScore.yakumanCount
        )

        return ScoreCalculationResponseDto(
            handId = handId,
            success = true,
            message = "점수 계산 완료 (저장하지 않음)",
            score = scoreResult
        )
    }

    // 점수 계산 후 저장
    fun calculateAndSaveScore(handId: Int): ScoreCalculationResponseDto {
        val hand = handRepository.findById(handId).orElse(null)
            ?: return ScoreCalculationResponseDto(
                handId = handId,
                success = false,
                message = "핸드를 찾을 수 없습니다."
            )

        // 기존 Score가 있는지 확인
        val existingScore = scoreRepository.findByHandId(handId)
        if (existingScore != null) {
            return ScoreCalculationResponseDto(
                handId = handId,
                success = false,
                message = "이미 점수가 계산된 핸드입니다."
            )
        }

        // 실제 Hand 데이터를 기반으로 점수 계산
        val calculatedScore = calculateActualScore(hand)

        // 계산된 점수로 Score 생성 및 저장
        val score = Score(
            hand = hand,
            totalFu = calculatedScore.totalFu,
            totalPan = calculatedScore.totalPan,
            basePoint = calculatedScore.basePoint,
            dealerTsumoPoint = calculatedScore.dealerTsumoPoint,
            dealerRonPoint = calculatedScore.dealerRonPoint,
            nonDealerTsumoPoint = calculatedScore.nonDealerTsumoPoint,
            nonDealerRonPoint = calculatedScore.nonDealerRonPoint,
            honbaBonus = calculatedScore.honbaBonus,
            totalPoint = calculatedScore.totalPoint,
            isYakuman = calculatedScore.isYakuman,
            yakumanCount = calculatedScore.yakumanCount,
            doraTotalCount = calculatedScore.doraTotalCount
        )

        val savedScore = scoreRepository.save(score)

        // 저장된 Score를 DTO로 변환
        val scoreResult = ScoreResultDto(
            totalPoint = savedScore.totalPoint,
            dealerTsumoPoint = savedScore.dealerTsumoPoint,
            dealerRonPoint = savedScore.dealerRonPoint,
            nonDealerTsumoPoint = savedScore.nonDealerTsumoPoint,
            nonDealerRonPoint = savedScore.nonDealerRonPoint,
            honbaBonus = savedScore.honbaBonus,
            isYakuman = savedScore.isYakuman,
            yakumanCount = savedScore.yakumanCount
        )

        return ScoreCalculationResponseDto(
            handId = handId,
            success = true,
            message = "점수 계산 및 저장 완료",
            score = scoreResult
        )
    }

    // 실제 마작 점수 계산 로직 (DB 데이터 기반)
    private fun calculateActualScore(hand: Hand): ScoreCalculationResult {
        // 1. Hand에 연결된 HandYaku들을 조회하여 역 확인
        val handYakus = hand.handYakus
        val handFus = hand.handFus

        // 2. 기본 역 자동 계산 (DB에 HandYaku가 없을 경우)
        var totalPan = 0

        // 2.1 도라 계산 (Hand 테이블에 저장된 값 사용)
        totalPan += hand.doraCount + hand.uraDoraCount + hand.redDoraCount

        // 2.2 기본 역 자동 추가 (HandYaku 테이블이 비어있을 경우)
        if (handYakus.isEmpty()) {
            // 쯔모 시 멘젠청자모 1판
            if (hand.isTsumo) {
                totalPan += 1
            }

            // 리치 시 1판
            if (hand.isReach) {
                totalPan += 1
            }

            // 기본 1판 (탕야오 가정)
            totalPan += 1
        } else {
            // HandYaku가 있으면 실제 데이터 사용
            totalPan += handYakus.sumOf { it.yakuRule.pan }
        }

        // 3. 부수 계산
        var totalFu = 20 // 기본 20부

        if (handFus.isEmpty()) {
            // HandFu가 없으면 기본 부수 계산
            if (hand.isTsumo) {
                totalFu += 2 // 쯔모 2부
            } else {
                totalFu += 10 // 론 10부 (멘젠)
            }

            // 각자 부수 추가 (888삭 가정)
            totalFu += 4 // 중장패 암각 4부

            // 100점 단위로 올림
            totalFu = ((totalFu + 9) / 10) * 10
        } else {
            // HandFu가 있으면 실제 데이터 사용
            totalFu += handFus.sumOf { it.fuRule.fuValue * it.count }
        }

        // 4. 역만 체크
        val isYakuman = handYakus.any { it.yakuRule.isYakuman }
        val yakumanCount = if (isYakuman) handYakus.count { it.yakuRule.isYakuman } else 0

        // 5. 기본 점수 계산
        val basePoint = if (isYakuman) {
            8000 * yakumanCount
        } else {
            // 마작 점수 공식: 부수 × 2^(판수+2)
            val rawPoint = totalFu * 2.0.pow((totalPan + 2).toDouble()).toInt()

            // 100점 단위로 올림 처리
            ((rawPoint + 99) / 100) * 100
        }

        // 6. 인원수에 따른 점수 분배 계산
        return when (hand.playerCount.toInt()) {
            3 -> calculateThreePlayerScore(basePoint, hand.isDealer, hand.isTsumo, hand.honbaCount, totalFu, totalPan, isYakuman, yakumanCount, hand.doraCount + hand.uraDoraCount + hand.redDoraCount)
            4 -> calculateFourPlayerScore(basePoint, hand.isDealer, hand.isTsumo, hand.honbaCount, totalFu, totalPan, isYakuman, yakumanCount, hand.doraCount + hand.uraDoraCount + hand.redDoraCount)
            else -> throw IllegalArgumentException("지원하지 않는 인원수: ${hand.playerCount}")
        }
    }

    // 4인 마작 점수 분배 (수정)
    private fun calculateFourPlayerScore(basePoint: Int, isDealer: Boolean, isTsumo: Boolean, honbaCount: Int, totalFu: Int, totalPan: Int, isYakuman: Boolean, yakumanCount: Int, doraTotalCount: Int): ScoreCalculationResult {
        val honbaBonus = honbaCount * 100

        return if (isTsumo) {
            if (isDealer) {
                // 오야 쯔모: 각자 1/3씩 지불 (100점 단위 올림)
                val eachPay = ((basePoint * 2 + 200) / 3 / 100) * 100
                val totalReceived = eachPay * 3
                ScoreCalculationResult(
                    totalPoint = totalReceived,
                    dealerTsumoPoint = eachPay + honbaBonus,
                    dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                    nonDealerTsumoPoint = eachPay + honbaBonus,
                    nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                    honbaBonus = honbaBonus,
                    isYakuman = isYakuman,
                    yakumanCount = yakumanCount,
                    totalFu = totalFu,
                    totalPan = totalPan,
                    basePoint = basePoint,
                    doraTotalCount = doraTotalCount
                )
            } else {
                // 코야 쯔모: 오야가 절반, 코야들이 1/4씩 (100점 단위 올림)
                val dealerPay = ((basePoint + 100) / 2 / 100) * 100
                val nonDealerPay = ((basePoint + 300) / 4 / 100) * 100
                val totalReceived = dealerPay + (nonDealerPay * 2)
                ScoreCalculationResult(
                    totalPoint = totalReceived,
                    dealerTsumoPoint = dealerPay + honbaBonus,
                    dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                    nonDealerTsumoPoint = nonDealerPay + honbaBonus,
                    nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                    honbaBonus = honbaBonus,
                    isYakuman = isYakuman,
                    yakumanCount = yakumanCount,
                    totalFu = totalFu,
                    totalPan = totalPan,
                    basePoint = basePoint,
                    doraTotalCount = doraTotalCount
                )
            }
        } else {
            // 론: 방총자가 전액 지불
            val ronPoint = if (isDealer) {
                ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus
            } else {
                (basePoint / 100) * 100 + honbaBonus
            }
            ScoreCalculationResult(
                totalPoint = ronPoint,
                dealerTsumoPoint = 0,
                dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                nonDealerTsumoPoint = 0,
                nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                honbaBonus = honbaBonus,
                isYakuman = isYakuman,
                yakumanCount = yakumanCount,
                totalFu = totalFu,
                totalPan = totalPan,
                basePoint = basePoint,
                doraTotalCount = doraTotalCount
            )
        }
    }

    // 3인 마작 점수 분배 (수정)
    private fun calculateThreePlayerScore(basePoint: Int, isDealer: Boolean, isTsumo: Boolean, honbaCount: Int, totalFu: Int, totalPan: Int, isYakuman: Boolean, yakumanCount: Int, doraTotalCount: Int): ScoreCalculationResult {
        val honbaBonus = honbaCount * 100

        return if (isTsumo) {
            if (isDealer) {
                // 오야 쯔모: 각자 절반씩 지불 (100점 단위 올림)
                val eachPay = ((basePoint + 100) / 2 / 100) * 100
                val totalReceived = eachPay * 2
                ScoreCalculationResult(
                    totalPoint = totalReceived,
                    dealerTsumoPoint = eachPay + honbaBonus,
                    dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                    nonDealerTsumoPoint = eachPay + honbaBonus,
                    nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                    honbaBonus = honbaBonus,
                    isYakuman = isYakuman,
                    yakumanCount = yakumanCount,
                    totalFu = totalFu,
                    totalPan = totalPan,
                    basePoint = basePoint,
                    doraTotalCount = doraTotalCount
                )
            } else {
                // 코야 쯔모: 오야가 절반, 코야가 1/4 (100점 단위 올림)
                val dealerPay = ((basePoint + 100) / 2 / 100) * 100
                val nonDealerPay = ((basePoint + 200) / 4 / 100) * 100
                val totalReceived = dealerPay + nonDealerPay
                ScoreCalculationResult(
                    totalPoint = totalReceived,
                    dealerTsumoPoint = dealerPay + honbaBonus,
                    dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                    nonDealerTsumoPoint = nonDealerPay + honbaBonus,
                    nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                    honbaBonus = honbaBonus,
                    isYakuman = isYakuman,
                    yakumanCount = yakumanCount,
                    totalFu = totalFu,
                    totalPan = totalPan,
                    basePoint = basePoint,
                    doraTotalCount = doraTotalCount
                )
            }
        } else {
            // 론: 방총자가 전액 지불
            val ronPoint = if (isDealer) {
                ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus
            } else {
                (basePoint / 100) * 100 + honbaBonus
            }
            ScoreCalculationResult(
                totalPoint = ronPoint,
                dealerTsumoPoint = 0,
                dealerRonPoint = ((basePoint * 1.5).toInt() / 100) * 100 + honbaBonus,
                nonDealerTsumoPoint = 0,
                nonDealerRonPoint = (basePoint / 100) * 100 + honbaBonus,
                honbaBonus = honbaBonus,
                isYakuman = isYakuman,
                yakumanCount = yakumanCount,
                totalFu = totalFu,
                totalPan = totalPan,
                basePoint = basePoint,
                doraTotalCount = doraTotalCount
            )
        }
    }


    fun deleteHand(handId: Int): Boolean {
        return try {
            if (handRepository.existsById(handId)) {
                handRepository.deleteById(handId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getHandStatistics(): HandStatisticsDto {
        val allHands = handRepository.findAll().toList()
        val scores = scoreRepository.findAll().toList()

        return HandStatisticsDto(
            totalHands = allHands.size.toLong(),
            yakumanHands = allHands.count { hand -> hand.score?.isYakuman == true }.toLong(),
            reachHands = allHands.count { hand -> hand.isReach }.toLong(),
            dealerHands = allHands.count { hand -> hand.isDealer }.toLong(),
            tsumoHands = allHands.count { hand -> hand.isTsumo }.toLong(),
            averageScore = scores.map { score -> score.totalPoint }.takeIf { list -> list.isNotEmpty() }?.average(),
            highestScore = scores.maxOfOrNull { score -> score.totalPoint },
            lowestScore = scores.minOfOrNull { score -> score.totalPoint }
        )
    }
}

// 점수 계산 결과를 담는 데이터 클래스
data class ScoreCalculationResult(
    val totalPoint: Int,
    val dealerTsumoPoint: Int,
    val dealerRonPoint: Int,
    val nonDealerTsumoPoint: Int,
    val nonDealerRonPoint: Int,
    val honbaBonus: Int,
    val isYakuman: Boolean,
    val yakumanCount: Int,
    val totalFu: Int,
    val totalPan: Int,
    val basePoint: Int,
    val doraTotalCount: Int
)



@Service
class MajhongService(
    private val handRepository: HandRepository,
    private val scoreRepository: ScoreRepository
)



@Service
class YakuAnalyzer {
    fun calculateTotalPan(hand: Hand): Int {
        var totalPan = 0

        // 기본 역들
        if (hand.isReach) totalPan += 1
        if (hand.isTsumo && !hasOpenMelds(hand)) totalPan += 1

        // 도라 추가
        totalPan += hand.doraCount + hand.uraDoraCount + hand.redDoraCount

        // 실제 역 데이터에서 판수 추가
        totalPan += hand.handYakus.sumOf { it.yakuRule.pan }

        return totalPan
    }

    private fun hasOpenMelds(hand: Hand): Boolean {
        return hand.handTiles.any { it.isFromFuro }
    }
}


@Service
class MahjongScoreCalculator {

    // 기본 점수 계산: 2^(판+2) × 부수
    fun calculateBaseScore(pan: Int, fu: Int): Int {
        return if (pan >= 5) {
            // 5판 이상은 고정 점수
            when (pan) {
                5 -> 2000        // 만관
                6, 7 -> 3000     // 하네만
                in 8..10 -> 4000 // 배만
                11, 12 -> 6000   // 삼배만
                else -> 8000     // 역만 (13판 이상)
            }
        } else {
            // 1-4판: 2^(판+2) × 부수
            val baseScore = fu * (1 shl (pan + 2))
            minOf(baseScore, 2000) // 기본점은 2000점 상한
        }
    }

    // 부수 올림 처리 (10의 자리에서 올림)
    fun roundFu(fu: Int): Int {
        return ((fu + 9) / 10) * 10
    }

    // 점수 올림 처리 (100의 자리에서 올림)
    fun roundScore(score: Int): Int {
        return ((score + 99) / 100) * 100
    }

    // 최종 점수 계산
    fun calculateFinalScore(baseScore: Int, isDealer: Boolean, isTsumo: Boolean): ScoreResult {
        return when {
            // 오야(친) 쯔모
            isDealer && isTsumo -> {
                val paymentPerPlayer = roundScore(baseScore * 2)
                ScoreResult(
                    totalScore = paymentPerPlayer * 3,
                    dealerTsumoPoint = paymentPerPlayer,
                    dealerRonPoint = 0,
                    nonDealerTsumoPoint = 0,
                    nonDealerRonPoint = 0
                )
            }
            // 오야(친) 론
            isDealer && !isTsumo -> {
                val totalPayment = roundScore(baseScore * 6)
                ScoreResult(
                    totalScore = totalPayment,
                    dealerTsumoPoint = 0,
                    dealerRonPoint = totalPayment,
                    nonDealerTsumoPoint = 0,
                    nonDealerRonPoint = 0
                )
            }
            // 자 쯔모
            !isDealer && isTsumo -> {
                val paymentFromDealer = roundScore(baseScore * 2)
                val paymentFromNonDealer = roundScore(baseScore)
                ScoreResult(
                    totalScore = paymentFromDealer + (paymentFromNonDealer * 2),
                    dealerTsumoPoint = 0,
                    dealerRonPoint = 0,
                    nonDealerTsumoPoint = paymentFromDealer, // 오야가 지불
                    nonDealerRonPoint = paymentFromNonDealer // 자가 지불
                )
            }
            // 자 론
            else -> {
                val totalPayment = roundScore(baseScore * 4)
                ScoreResult(
                    totalScore = totalPayment,
                    dealerTsumoPoint = 0,
                    dealerRonPoint = 0,
                    nonDealerTsumoPoint = 0,
                    nonDealerRonPoint = totalPayment
                )
            }
        }
    }
}

data class ScoreResult(
    val totalScore: Int,
    val dealerTsumoPoint: Int,
    val dealerRonPoint: Int,
    val nonDealerTsumoPoint: Int,
    val nonDealerRonPoint: Int
)

@Service
class FuCalculator {

    fun calculateTotalFu(hand: Hand): Int {
        var totalFu = 20 // 기본부

        // 멘젠 쯔모 +2부
        if (hand.isTsumo && !hasOpenMelds(hand)) {
            totalFu += 2
        }

        // 각자/안커 부수 계산
        totalFu += calculateMeldFu(hand)

        // 머리 부수 계산
        totalFu += calculatePairFu(hand)

        // 대기 부수 (단순화: 단기대기 +2부, 나머지 +0부)
        totalFu += calculateWaitFu(hand)

        // 부수 올림 처리
        return roundFu(totalFu)
    }

    private fun hasOpenMelds(hand: Hand): Boolean {
        return hand.handTiles.any { it.isFromFuro }
    }

    private fun calculateMeldFu(hand: Hand): Int {
        var fu = 0
        val tiles = hand.handTiles.groupBy { it.groupIndex }

        tiles.forEach { (groupIndex, groupTiles) ->
            if (groupIndex > 0 && groupTiles.size >= 3) { // 면자 (머리 제외)
                val tile = groupTiles.first().tile
                val isTerminal = isTerminalOrHonor(tile)
                val isConcealed = groupTiles.none { it.isFromFuro }

                when (groupTiles.size) {
                    3 -> { // 각자
                        fu += if (isConcealed) {
                            if (isTerminal) 8 else 4 // 안커
                        } else {
                            if (isTerminal) 4 else 2 // 명각
                        }
                    }
                    4 -> { // 깡
                        fu += if (isConcealed) {
                            if (isTerminal) 32 else 16 // 암깡
                        } else {
                            if (isTerminal) 16 else 8 // 명깡/가깡
                        }
                    }
                }
            }
        }
        return fu
    }

    private fun calculatePairFu(hand: Hand): Int {
        val pairTiles = hand.handTiles.filter { it.groupIndex == 0 }
        if (pairTiles.size == 2) {
            val tile = pairTiles.first().tile
            // 자풍패, 장풍패, 삼원패 머리 +2부
            if (isValueHonor(tile, hand)) {
                return 2
            }
        }
        return 0
    }

    private fun calculateWaitFu(hand: Hand): Int {
        // 단순화: 기본적으로 단기대기로 가정 (+2부)
        // 실제로는 화료패와 패 조합을 분석해야 함
        return 2
    }

    private fun isTerminalOrHonor(tile: Tile): Boolean {
        return when (tile.type) {
            "자패" -> true
            "만수", "통수", "삭수" -> tile.number == 1 || tile.number == 9
            else -> false
        }
    }

    private fun isValueHonor(tile: Tile, hand: Hand): Boolean {
        if (tile.type != "자패") return false

        return when (tile.number) {
            1, 2, 3, 4 -> { // 동남서북
                tile.number == getWindNumber(hand.playerWind) ||
                        tile.number == getWindNumber(hand.roundWind)
            }
            5, 6, 7 -> true // 백발중 (삼원패)
            else -> false
        }
    }

    private fun getWindNumber(wind: Wind?): Int {
        return when (wind) {
            Wind.동 -> 1
            Wind.남 -> 2
            Wind.서 -> 3
            Wind.북 -> 4
            else -> 0
        }
    }

    private fun roundFu(fu: Int): Int {
        return ((fu + 9) / 10) * 10
    }
}

@Service
class YakuRuleService(
    private val yakuRuleRepository: YakuRuleRepository
) {
    fun getAllYakuRules(): List<YakuDto> {
        return yakuRuleRepository.findAll().map { yakuRule ->
            YakuDto(
                id = yakuRule.id,
                name = yakuRule.name,
                pan = yakuRule.pan,
                isMenzen = yakuRule.isMenzen,
                isYakuman = yakuRule.isYakuman,
                description = yakuRule.description
            )
        }
    }

    fun getYakumanRules(): List<YakuDto> {
        return yakuRuleRepository.findByIsYakuman(true).map { yakuRule ->
            YakuDto(
                id = yakuRule.id,
                name = yakuRule.name,
                pan = yakuRule.pan,
                isMenzen = yakuRule.isMenzen,
                isYakuman = yakuRule.isYakuman,
                description = yakuRule.description
            )
        }
    }

    fun getHighPanYakus(minPan: Int): List<YakuDto> {
        return yakuRuleRepository.findByPanGreaterThanEqual(minPan).map { yakuRule ->
            YakuDto(
                id = yakuRule.id,
                name = yakuRule.name,
                pan = yakuRule.pan,
                isMenzen = yakuRule.isMenzen,
                isYakuman = yakuRule.isYakuman,
                description = yakuRule.description
            )
        }
    }

    fun getYakuStatistics(): List<YakuStatisticsDto> {
        return yakuRuleRepository.getYakuStatistics()
    }
}

@Service
class FuRuleService(
    private val fuRuleRepository: FuRuleRepository
) {
    fun getAllFuRules(): List<FuDto> {
        return fuRuleRepository.findAll().map { fuRule ->
            FuDto(
                id = fuRule.id,
                name = fuRule.name,
                description = fuRule.description,
                fuValue = fuRule.fuValue,
                category = fuRule.category.name,
                count = 1
            )
        }
    }

    fun getFuRulesByCategory(category: String): List<FuDto> {
        val fuCategory = try {
            FuCategory.valueOf(category.uppercase())
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return fuRuleRepository.findByCategory(fuCategory).map { fuRule ->
            FuDto(
                id = fuRule.id,
                name = fuRule.name,
                description = fuRule.description,
                fuValue = fuRule.fuValue,
                category = fuRule.category.name,
                count = 1
            )
        }
    }

    fun getMenzenOnlyRules(): List<FuDto> {
        return fuRuleRepository.findMenzenOnlyRules().map { fuRule ->
            FuDto(
                id = fuRule.id,
                name = fuRule.name,
                description = fuRule.description,
                fuValue = fuRule.fuValue,
                category = fuRule.category.name,
                count = 1
            )
        }
    }
}

@Service
class TileService(
    private val tileRepository: TileRepository
) {
    fun getAllTiles(): List<TileDto> {
        return tileRepository.findAll().map { tile ->
            TileDto(
                type = tile.type,
                number = tile.number,
                isRedDora = tile.isRedDora
            )
        }
    }

    fun getTilesByType(type: String): List<TileDto> {
        return tileRepository.findByType(type).map { tile ->
            TileDto(
                type = tile.type,
                number = tile.number,
                isRedDora = tile.isRedDora
            )
        }
    }

    fun getRedDoraTiles(): List<TileDto> {
        return tileRepository.findByIsRedDora(true).map { tile ->
            TileDto(
                type = tile.type,
                number = tile.number,
                isRedDora = tile.isRedDora
            )
        }
    }
}

@Service
class ScoreService(
    private val scoreRepository: ScoreRepository
) {
    fun getHighScores(limit: Int = 10): List<ScoreResultDto> {
        return scoreRepository.findTopScores(limit).map { score ->
            ScoreResultDto(
                totalPoint = score.totalPoint,
                dealerTsumoPoint = score.dealerTsumoPoint,
                dealerRonPoint = score.dealerRonPoint,
                nonDealerTsumoPoint = score.nonDealerTsumoPoint,
                nonDealerRonPoint = score.nonDealerRonPoint,
                honbaBonus = score.honbaBonus,
                isYakuman = score.isYakuman,
                yakumanCount = score.yakumanCount
            )
        }
    }

    fun getAverageScore(): Double {
        return scoreRepository.getAverageScore() ?: 0.0
    }

    fun getScoreStatistics(): Map<String, Any> {
        val scores = scoreRepository.findAll().toList()

        return mapOf(
            "totalScores" to scores.size,
            "averageScore" to (scores.map { it.totalPoint }.takeIf { it.isNotEmpty() }?.average() ?: 0.0),
            "highestScore" to (scores.maxOfOrNull { it.totalPoint } ?: 0),
            "lowestScore" to (scores.minOfOrNull { it.totalPoint } ?: 0),
            "yakumanCount" to scores.count { it.isYakuman }
        )
    }
}