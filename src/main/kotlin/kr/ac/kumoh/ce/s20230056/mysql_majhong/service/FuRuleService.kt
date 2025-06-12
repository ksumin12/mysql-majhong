package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.FuRule
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.FuRuleRepository
import org.springframework.stereotype.Service

@Service
class FuRuleService(val repository: FuRuleRepository) {
    fun getAllFuRules(): List<FuRule> = repository.findAll()
}
