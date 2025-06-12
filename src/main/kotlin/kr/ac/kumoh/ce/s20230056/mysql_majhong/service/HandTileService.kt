package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandTile
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.HandTileRepository
import org.springframework.stereotype.Service

@Service
class HandTileService(val repository: HandTileRepository) {
    fun getAllHandTiles(): List<HandTile> = repository.findAll()
}
