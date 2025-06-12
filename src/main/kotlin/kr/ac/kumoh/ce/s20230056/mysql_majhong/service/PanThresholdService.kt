package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.PanThreshold
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.PanThresholdRepository
import org.springframework.stereotype.Service

@Service
class PanThresholdService(val repository: PanThresholdRepository) {
    fun getAllThresholds(): List<PanThreshold> = repository.findAll()
}

