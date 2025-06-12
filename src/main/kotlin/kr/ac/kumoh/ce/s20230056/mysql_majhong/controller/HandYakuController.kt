package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandYaku
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.HandYakuService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/handyakus")
class HandYakuController(val service: HandYakuService) {
    @GetMapping
    fun getAllHandYakus(): ResponseEntity<List<HandYaku>> {
        val results = service.getAllHandYakus()
        return ResponseEntity.ok(results)
    }
}