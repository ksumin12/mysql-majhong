package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Tile
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.TileRepository
import org.springframework.stereotype.Service

@Service
class TileService(val repository: TileRepository) {
    fun getAllTiles(): List<Tile> = repository.findAll()
}
