package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandFu
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.HandFuService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/handfu")
class HandFuController(val service: HandFuService) {
    @GetMapping
    fun getAllHandFu(): ResponseEntity<List<HandFu>> {
        val results = service.getAllHandFu()
        return ResponseEntity.ok(results)
    }
}
