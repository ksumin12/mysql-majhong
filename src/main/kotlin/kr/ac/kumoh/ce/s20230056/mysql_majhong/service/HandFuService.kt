package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandFu
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.HandFuRepository
import org.springframework.stereotype.Service

@Service
class HandFuService(val repository: HandFuRepository) {
    fun getAllHandFu(): List<HandFu> = repository.findAll()
}
