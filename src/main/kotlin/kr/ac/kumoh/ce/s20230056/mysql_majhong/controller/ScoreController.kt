package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Score
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.ScoreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/scores")
class ScoreController(val service: ScoreService) {
    @GetMapping
    fun getAllScores(): ResponseEntity<List<Score>> {
        val results = service.getAllScores()
        return ResponseEntity.ok(results)
    }
}

