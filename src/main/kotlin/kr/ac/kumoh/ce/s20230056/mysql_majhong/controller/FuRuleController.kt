package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.FuRule
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.FuRuleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/furules")
class FuRuleController(val service: FuRuleService) {
    @GetMapping
    fun getAllFuRules(): ResponseEntity<List<FuRule>> {
        val rules = service.getAllFuRules()
        return ResponseEntity.ok(rules)
    }
}