package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.YakuRule
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.YakuRuleRepository
import org.springframework.stereotype.Service

@Service
class YakuRuleService(val repository: YakuRuleRepository) {
    fun getAllYakuRules(): List<YakuRule> = repository.findAll()
}
