package kr.ac.kumoh.ce.s20230056.mysql_majhong.controller

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Tile
import kr.ac.kumoh.ce.s20230056.mysql_majhong.service.TileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tiles")
class TileController(val service: TileService) {
    @GetMapping
    fun getAllTiles(): ResponseEntity<List<Tile>> {
        val tiles = service.getAllTiles()
        return ResponseEntity.ok(tiles)
    }
}
