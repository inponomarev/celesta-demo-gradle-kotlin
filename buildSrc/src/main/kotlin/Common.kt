import ru.curs.celesta.CelestaException
import ru.curs.celesta.score.AbstractScore
import ru.curs.celesta.score.Grain
import ru.curs.celesta.score.ParseException
import ru.curs.celesta.score.Score
import ru.curs.celesta.score.discovery.ScoreByScorePathDiscovery

fun initScore(scorePath: String): Score {
    return try {
        AbstractScore.ScoreBuilder(Score::class.java)
            .scoreDiscovery(ScoreByScorePathDiscovery(scorePath))
            .build()
    } catch (e: ParseException) {
        throw CelestaException(e.message, e)
    }
}

fun isAllowGrain(grain: Grain): Boolean {
    return grain.getScore().getSysSchemaName() != grain.getName()
}