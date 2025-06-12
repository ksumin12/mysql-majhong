package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Hand
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.HandRepository
import org.springframework.stereotype.Service

@Service
class HandService(val repository: HandRepository) {
    fun getAllHands(): List<Hand> = repository.findAll()
}
