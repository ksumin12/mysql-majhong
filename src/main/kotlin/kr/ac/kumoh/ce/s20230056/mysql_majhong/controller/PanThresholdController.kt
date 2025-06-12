package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.PanThreshold
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.PanThresholdService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/panthresholds")
class PanThresholdController(val service: PanThresholdService) {
    @GetMapping
    fun getAllThresholds(): ResponseEntity<List<PanThreshold>> {
        val thresholds = service.getAllThresholds()
        return ResponseEntity.ok(thresholds)
    }
}
