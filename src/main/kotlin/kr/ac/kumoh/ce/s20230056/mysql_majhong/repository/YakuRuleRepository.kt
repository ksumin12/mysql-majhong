package kr.ac.kumoh.ce.s20230056.mysql_majhong.repository

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.YakuRule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface YakuRuleRepository : JpaRepository<YakuRule, Int>
