package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.HandYaku
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.HandYakuRepository
import org.springframework.stereotype.Service

@Service
class HandYakuService(val repository: HandYakuRepository) {
    fun getAllHandYakus(): List<HandYaku> = repository.findAll()
}
