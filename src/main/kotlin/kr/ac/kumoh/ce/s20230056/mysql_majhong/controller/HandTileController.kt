package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandTile
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.HandTileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/handtiles")
class HandTileController(val service: HandTileService) {
    @GetMapping
    fun getAllHandTiles(): ResponseEntity<List<HandTile>> {
        val results = service.getAllHandTiles()
        return ResponseEntity.ok(results)
    }
}
