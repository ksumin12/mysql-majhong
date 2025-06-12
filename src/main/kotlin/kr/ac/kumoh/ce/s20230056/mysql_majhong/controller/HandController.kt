package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Hand
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.HandService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/hands")
class HandController(val service: HandService) {
    @GetMapping
    fun getAllHands(): ResponseEntity<List<Hand>> {
        val hands = service.getAllHands()
        return ResponseEntity.ok(hands)
    }
}

