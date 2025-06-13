package kr.ac.kumoh.ce.s20230056.mysql_majhong

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
interface HandRepository : CrudRepository<Hand, Int> {

    // 강의자료 조건: SELECT 뒤에 new 키워드, FQCN 사용, alias.필드명 형식
    // 1. 모든 핸드 조회
    @Query("""
    SELECT new kr.ac.kumoh.ce.s20230056.mysql_majhong.EquimentDto(
        h.id,
        h.isTsumo,
        h.playerId,
        h.round,
        h.isReach,
        h.isDealer,
        h.playerCount,
        h.northDoraCount,
        h.playerWind,
        h.roundWind,
        h.honbaCount,
        h.doraCount,
        h.uraDoraCount,
        h.redDoraCount,
        yr.name,
        yr.pan,
        fr.fuValue,
        s.totalFu,
        s.totalPan,
        s.basePoint,
        s.dealerTsumoPoint,
        s.dealerRonPoint,
        s.nonDealerTsumoPoint,
        s.nonDealerRonPoint,
        s.honbaBonus,
        s.totalPoint,
        s.isYakuman,
        s.yakumanCount,
        s.doraTotalCount,
        h.createdAt
    )
    FROM Hand h
    LEFT JOIN h.handYakus hy
    LEFT JOIN hy.yakuRule yr
    LEFT JOIN h.handFus hf
    LEFT JOIN hf.fuRule fr
    LEFT JOIN h.score s
""")
    fun getHandDetails(): List<EquimentDto>  // 반환 타입도 EquimentDto

    // 2. 특정 플레이어 핸드 조회
    @Query("""
    SELECT new kr.ac.kumoh.ce.s20230056.mysql_majhong.EquimentDto(
        h.id,
        h.isTsumo,
        h.playerId,
        h.round,
        h.isReach,
        h.isDealer,
        h.playerCount,
        h.northDoraCount,
        h.playerWind,
        h.roundWind,
        h.honbaCount,
        h.doraCount,
        h.uraDoraCount,
        h.redDoraCount,
        yr.name,
        yr.pan,
        fr.fuValue,
        s.totalFu,
        s.totalPan,
        s.basePoint,
        s.dealerTsumoPoint,
        s.dealerRonPoint,
        s.nonDealerTsumoPoint,
        s.nonDealerRonPoint,
        s.honbaBonus,
        s.totalPoint,
        s.isYakuman,
        s.yakumanCount,
        s.doraTotalCount,
        h.createdAt
    )
    FROM Hand h
    LEFT JOIN h.handYakus hy
    LEFT JOIN hy.yakuRule yr
    LEFT JOIN h.handFus hf
    LEFT JOIN hf.fuRule fr
    LEFT JOIN h.score s
    WHERE h.playerId = :playerId
""")
    fun getHandDetailsByPlayerId(@Param("playerId") playerId: Int): List<EquimentDto>

    // 특정 역을 가진 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        JOIN h.handYakus hy
        JOIN hy.yakuRule yr
        WHERE yr.name = :yakuName
    """)
    fun findHandsByYakuName(@Param("yakuName") yakuName: String): List<Hand>

    // 오야의 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        WHERE h.isDealer = true
    """)
    fun findDealerHands(): List<Hand>

    // 특정 패 타입을 가진 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        JOIN h.handTiles ht
        JOIN ht.tile t
        WHERE t.type = :tileType
    """)
    fun findHandsByTileType(@Param("tileType") tileType: String): List<Hand>

    // 리치한 핸드 개수 조회
    @Query("""
        SELECT COUNT(h) FROM Hand h
        WHERE h.round = :round AND h.isReach = true
    """)
    fun countReachHandsByRound(@Param("round") round: String): Long

    // 역만 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        JOIN h.score s
        WHERE s.isYakuman = true
    """)
    fun findYakumanHands(): List<Hand>

    // 쯔모 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        WHERE h.isTsumo = true
    """)
    fun findTsumoHands(): List<Hand>

    // 특정 라운드의 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        WHERE h.round = :round
    """)
    fun findHandsByRound(@Param("round") round: String): List<Hand>

    // 플레이어별 핸드 개수 조회
    @Query("""
        SELECT COUNT(h) FROM Hand h
        WHERE h.playerId = :playerId
    """)
    fun countHandsByPlayerId(@Param("playerId") playerId: Int): Long

    // 최근 핸드 조회
    @Query("""
        SELECT h FROM Hand h
        ORDER BY h.createdAt DESC
    """)
    fun findRecentHands(): List<Hand>
}

interface YakuRuleRepository : CrudRepository<YakuRule, Int> {

    // 역만 여부로 조회
    @Query("SELECT yr FROM YakuRule yr WHERE yr.isYakuman = :isYakuman")
    fun findByIsYakuman(@Param("isYakuman") isYakuman: Boolean): List<YakuRule>

    // 최소 판수 이상의 역 조회
    @Query("SELECT yr FROM YakuRule yr WHERE yr.pan >= :minPan")
    fun findByPanGreaterThanEqual(@Param("minPan") minPan: Int): List<YakuRule>

    // 멘젠 전용 역 조회
    @Query("SELECT yr FROM YakuRule yr WHERE yr.isMenzen = true")
    fun findMenzenOnlyYakus(): List<YakuRule>

    // 역 이름으로 조회
    @Query("SELECT yr FROM YakuRule yr WHERE yr.name = :name")
    fun findByName(@Param("name") name: String): YakuRule?

    // 판수별 역 조회
    @Query("SELECT yr FROM YakuRule yr WHERE yr.pan = :pan")
    fun findByPan(@Param("pan") pan: Int): List<YakuRule>

    // 역 통계 조회
    @Query("""
        SELECT new kr.ac.kumoh.ce.s20230056.mysql_majhong.YakuStatisticsDto(
            yr.name,
            COUNT(hy),
            (COUNT(hy) * 100.0 / (SELECT COUNT(h) FROM Hand h))
        )
        FROM YakuRule yr
        LEFT JOIN yr.handYakus hy
        GROUP BY yr.id, yr.name
        ORDER BY COUNT(hy) DESC
    """)
    fun getYakuStatistics(): List<YakuStatisticsDto>
}

interface FuRuleRepository : CrudRepository<FuRule, Int> {

    // 카테고리별 부 규칙 조회
    @Query("SELECT fr FROM FuRule fr WHERE fr.category = :category")
    fun findByCategory(@Param("category") category: FuCategory): List<FuRule>

    // 멘젠 전용 부 규칙 조회
    @Query("SELECT fr FROM FuRule fr WHERE fr.isMenzenOnly = true")
    fun findMenzenOnlyRules(): List<FuRule>

    // 부 값 범위로 조회
    @Query("SELECT fr FROM FuRule fr WHERE fr.fuValue BETWEEN :minFu AND :maxFu")
    fun findByFuValueBetween(@Param("minFu") minFu: Int, @Param("maxFu") maxFu: Int): List<FuRule>

    // 부 이름으로 조회
    @Query("SELECT fr FROM FuRule fr WHERE fr.name = :name")
    fun findByName(@Param("name") name: String): FuRule?

    // 특정 부 값 조회
    @Query("SELECT fr FROM FuRule fr WHERE fr.fuValue = :fuValue")
    fun findByFuValue(@Param("fuValue") fuValue: Int): List<FuRule>
}

interface ScoreRepository : CrudRepository<Score, Int> {


    // 높은 점수 순으로 조회
    @Query("SELECT s FROM Score s ORDER BY s.totalPoint DESC")
    fun findTopScores(@Param("limit") limit: Int): List<Score>

    // 평균 점수 조회
    @Query("SELECT AVG(s.totalPoint) FROM Score s")
    fun getAverageScore(): Double?

    // 최고 점수 조회
    @Query("SELECT MAX(s.totalPoint) FROM Score s")
    fun getMaxScore(): Int?

    // 최저 점수 조회
    @Query("SELECT MIN(s.totalPoint) FROM Score s")
    fun getMinScore(): Int?

    // 특정 점수 이상 조회
    @Query("SELECT s FROM Score s WHERE s.totalPoint >= :minPoint")
    fun findByTotalPointGreaterThanEqual(@Param("minPoint") minPoint: Int): List<Score>

    // 역만 점수 조회
    @Query("SELECT s FROM Score s WHERE s.isYakuman = true")
    fun findYakumanScores(): List<Score>

    // 핸드 ID로 점수 조회
    @Query("SELECT s FROM Score s WHERE s.hand.id = :handId")
    fun findByHandId(@Param("handId") handId: Int): Score?

    // 점수 범위로 조회
    @Query("SELECT s FROM Score s WHERE s.totalPoint BETWEEN :minPoint AND :maxPoint")
    fun findByTotalPointBetween(@Param("minPoint") minPoint: Int, @Param("maxPoint") maxPoint: Int): List<Score>

    // 오야 점수 통계
    @Query("""
        SELECT AVG(s.totalPoint) FROM Score s
        JOIN s.hand h
        WHERE h.isDealer = true
    """)
    fun getAverageDealerScore(): Double?

    // 자 점수 통계
    @Query("""
        SELECT AVG(s.totalPoint) FROM Score s
        JOIN s.hand h
        WHERE h.isDealer = false
    """)
    fun getAverageNonDealerScore(): Double?
}

interface TileRepository : CrudRepository<Tile, Int> {

    // 패 타입으로 조회
    @Query("SELECT t FROM Tile t WHERE t.type = :type")
    fun findByType(@Param("type") type: String): List<Tile>

    // 적도라 여부로 조회
    @Query("SELECT t FROM Tile t WHERE t.isRedDora = :isRedDora")
    fun findByIsRedDora(@Param("isRedDora") isRedDora: Boolean): List<Tile>

    // 특정 패 조회
    @Query("SELECT t FROM Tile t WHERE t.type = :type AND t.number = :number AND t.isRedDora = :isRedDora")
    fun findByTypeAndNumberAndIsRedDora(
        @Param("type") type: String,
        @Param("number") number: Int,
        @Param("isRedDora") isRedDora: Boolean
    ): Tile?

    // 패 번호 범위로 조회
    @Query("SELECT t FROM Tile t WHERE t.type = :type AND t.number BETWEEN :minNumber AND :maxNumber")
    fun findByTypeAndNumberBetween(
        @Param("type") type: String,
        @Param("minNumber") minNumber: Int,
        @Param("maxNumber") maxNumber: Int
    ): List<Tile>

    // 자패 조회
    @Query("SELECT t FROM Tile t WHERE t.type = '자패'")
    fun findHonorTiles(): List<Tile>

    // 수패 조회
    @Query("SELECT t FROM Tile t WHERE t.type IN ('만수', '통수', '삭수')")
    fun findNumberTiles(): List<Tile>
}

interface HandTileRepository : CrudRepository<HandTile, Int> {

    // 핸드별 손패 조회
    @Query("SELECT ht FROM HandTile ht WHERE ht.hand.id = :handId")
    fun findByHandId(@Param("handId") handId: Int): List<HandTile>

    // 후로 패 조회
    @Query("SELECT ht FROM HandTile ht WHERE ht.hand.id = :handId AND ht.isFromFuro = true")
    fun findFuroTilesByHandId(@Param("handId") handId: Int): List<HandTile>

    // 화료패 조회
    @Query("SELECT ht FROM HandTile ht WHERE ht.hand.id = :handId AND ht.isWinningTile = true")
    fun findWinningTileByHandId(@Param("handId") handId: Int): HandTile?

    // 그룹별 패 조회
    @Query("SELECT ht FROM HandTile ht WHERE ht.hand.id = :handId AND ht.groupIndex = :groupIndex")
    fun findByHandIdAndGroupIndex(@Param("handId") handId: Int, @Param("groupIndex") groupIndex: Int): List<HandTile>

    // 특정 패 타입 조회
    @Query("""
        SELECT ht FROM HandTile ht
        JOIN ht.tile t
        WHERE ht.hand.id = :handId AND t.type = :tileType
    """)
    fun findByHandIdAndTileType(@Param("handId") handId: Int, @Param("tileType") tileType: String): List<HandTile>
}

interface HandYakuRepository : CrudRepository<HandYaku, Int> {

    // 핸드별 역 조회
    @Query("SELECT hy FROM HandYaku hy WHERE hy.hand.id = :handId")
    fun findByHandId(@Param("handId") handId: Int): List<HandYaku>

    // 특정 역의 핸드 조회
    @Query("SELECT hy FROM HandYaku hy WHERE hy.yakuRule.id = :yakuRuleId")
    fun findByYakuRuleId(@Param("yakuRuleId") yakuRuleId: Int): List<HandYaku>

    // 핸드의 총 판수 조회
    @Query("SELECT SUM(hy.yakuRule.pan) FROM HandYaku hy WHERE hy.hand.id = :handId")
    fun getTotalPanByHandId(@Param("handId") handId: Int): Int?

    // 역만 역 조회
    @Query("""
        SELECT hy FROM HandYaku hy
        JOIN hy.yakuRule yr
        WHERE hy.hand.id = :handId AND yr.isYakuman = true
    """)
    fun findYakumanByHandId(@Param("handId") handId: Int): List<HandYaku>
}

interface HandFuRepository : CrudRepository<HandFu, Int> {

    // 핸드별 부 조회
    @Query("SELECT hf FROM HandFu hf WHERE hf.hand.id = :handId")
    fun findByHandId(@Param("handId") handId: Int): List<HandFu>

    // 특정 부 규칙의 핸드 조회
    @Query("SELECT hf FROM HandFu hf WHERE hf.fuRule.id = :fuRuleId")
    fun findByFuRuleId(@Param("fuRuleId") fuRuleId: Int): List<HandFu>

    // 핸드의 총 부수 조회
    @Query("SELECT SUM(hf.fuRule.fuValue * hf.count) FROM HandFu hf WHERE hf.hand.id = :handId")
    fun getTotalFuByHandId(@Param("handId") handId: Int): Int?

    // 카테고리별 부 조회
    @Query("""
        SELECT hf FROM HandFu hf
        JOIN hf.fuRule fr
        WHERE hf.hand.id = :handId AND fr.category = :category
    """)
    fun findByHandIdAndCategory(@Param("handId") handId: Int, @Param("category") category: FuCategory): List<HandFu>
}

interface PanThresholdRepository : CrudRepository<PanThresholdTable, Int> {

    // 부와 판으로 점수 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.fu = :fu AND pt.pan = :pan")
    fun findByFuAndPan(@Param("fu") fu: Int, @Param("pan") pan: Int): PanThresholdTable?

    // 특정 판수의 점수표 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.pan = :pan ORDER BY pt.fu")
    fun findByPan(@Param("pan") pan: Int): List<PanThresholdTable>

    // 특정 부수의 점수표 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.fu = :fu ORDER BY pt.pan")
    fun findByFu(@Param("fu") fu: Int): List<PanThresholdTable>

    // 점수 범위로 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.dealerRonPoint BETWEEN :minPoint AND :maxPoint")
    fun findByDealerRonPointBetween(@Param("minPoint") minPoint: Int, @Param("maxPoint") maxPoint: Int): List<PanThresholdTable>

    // 만관 이상 점수표 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.pan >= 5")
    fun findManganAndAbove(): List<PanThresholdTable>

    // 역만 점수표 조회
    @Query("SELECT pt FROM PanThresholdTable pt WHERE pt.pan >= 13")
    fun findYakumanThresholds(): List<PanThresholdTable>
}
