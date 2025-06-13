package kr.ac.kumoh.ce.s20230056.mysql_majhong

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/hands")
class HandController(val handService: HandService) {

    @GetMapping
    fun getAllHands(): ResponseEntity<List<EquimentDto>> {
        val hands = handService.getHandDetails()
        return ResponseEntity.ok(hands)
    }

    @GetMapping("/{id}")
    fun getHandById(@PathVariable id: Int): ResponseEntity<EquimentDto?> {
        val hands = handService.getHandDetails()
        val hand = hands.find { it.handId == id.toLong() }
        return ResponseEntity.ok(hand)
    }

    @PostMapping
    fun createHand(@RequestBody request: HandCreateRequestDto): ResponseEntity<String> {
        return ResponseEntity.ok("Hand received with ${request.tiles?.size ?: 0} tiles")
    }

    // 점수 계산만 (저장하지 않음)
    @GetMapping("/{id}/calculate")
    fun calculateScoreOnly(@PathVariable id: Int): ResponseEntity<ScoreCalculationResponseDto> {
        val result = handService.calculateScoreOnly(id)
        return ResponseEntity.ok(result)
    }

    // 점수 계산 후 저장
    @PostMapping("/{id}/calculate-and-save")
    fun calculateAndSaveScore(@PathVariable id: Int): ResponseEntity<ScoreCalculationResponseDto> {
        val result = handService.calculateAndSaveScore(id)
        return ResponseEntity.ok(result)
    }

    // 통계 조회
    @GetMapping("/statistics")
    fun getHandStatistics(): ResponseEntity<HandStatisticsDto> {
        val statistics = handService.getHandStatistics()
        return ResponseEntity.ok(statistics)
    }
}
