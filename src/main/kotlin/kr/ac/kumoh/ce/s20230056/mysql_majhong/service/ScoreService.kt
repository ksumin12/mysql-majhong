package kr.ac.kumoh.ce.s20230056.mysql_majhong.service

import kr.ac.kumoh.ce.s20230056.mysql_majhong.entity.Score
import kr.ac.kumoh.ce.s20230056.mysql_majhong.repository.ScoreRepository
import org.springframework.stereotype.Service

@Service
class ScoreService(val repository: ScoreRepository) {
    fun getAllScores(): List<Score> = repository.findAll()
}
