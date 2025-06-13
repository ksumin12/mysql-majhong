package kr.ac.kumoh.ce.s20230056.mysql_majhong

import org.springframework.stereotype.Service

@Service
class TileService(private val repository: TileRepository) {
    fun getAllTiles(): List<Tile> = repository.findAll()
}