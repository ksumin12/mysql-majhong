package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.YakuRule
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.YakuRuleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/yakurules")
class YakuRuleController(val service: YakuRuleService) {
    @GetMapping
    fun getAllYakuRules(): ResponseEntity<List<YakuRule>> {
        val rules = service.getAllYakuRules()
        return ResponseEntity.ok(rules)
    }
}
