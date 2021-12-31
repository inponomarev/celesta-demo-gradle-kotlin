import org.gradle.api.Project
import ru.curs.celesta.plugin.maven.CursorGenerator
import ru.curs.celesta.score.Grain
import ru.curs.celesta.score.GrainElement
import ru.curs.celesta.score.GrainPart
import ru.curs.celesta.score.MaterializedView
import ru.curs.celesta.score.ParameterizedView
import ru.curs.celesta.score.ReadOnlyTable
import ru.curs.celesta.score.Score
import ru.curs.celesta.score.SequenceElement
import ru.curs.celesta.score.Table
import ru.curs.celesta.score.View
import ru.curs.celesta.score.io.FileResource
import ru.curs.celesta.score.io.Resource
import java.io.File
import java.util.*
import java.util.function.Consumer

class CodeGenerator(
    private val project: Project
) {

    fun execute() {
        processScore("src/main/celestasql")
    }

    private fun processScore(properties: String) {
        val scorePath: String =
            project.layout.projectDirectory.asFile.absolutePath.toString() +
                    File.separator.toString() +
                    properties
        val score: Score = initScore(scorePath)
        score.grains.values
            .stream()
            .filter(::isAllowGrain)
            .forEach { g: Grain -> generateCursors(g, scorePath) }
    }

    private fun generateCursors(g: Grain, scorePath: String) {
        val isSysSchema = g.getName() == g.getScore().getSysSchemaName()
        val partsToElements: MutableMap<GrainPart, MutableList<GrainElement>> =
            HashMap<GrainPart, MutableList<GrainElement>>()
        val elements: MutableList<GrainElement> = ArrayList<GrainElement>()
        elements.addAll(g.getElements(SequenceElement::class.java).values)
        elements.addAll(g.getElements(Table::class.java).values)
        elements.addAll(g.getElements(ReadOnlyTable::class.java).values)
        elements.addAll(g.getElements(View::class.java).values)
        elements.addAll(g.getElements(MaterializedView::class.java).values)
        elements.addAll(g.getElements(ParameterizedView::class.java).values)
        elements.forEach(
            Consumer { ge: GrainElement ->
                partsToElements.computeIfAbsent(
                    ge.getGrainPart()
                ) { ArrayList<GrainElement>() }
                    .add(ge)
            }
        )
        val generator = CursorGenerator(sourceRoot(), true)
        partsToElements.entries.stream().forEach { (key, value): Map.Entry<GrainPart, List<GrainElement>> ->
            val sp: String
            sp = if (isSysSchema) {
                ""
            } else {
                val grainPartSource: Resource = key.getSource()
                val scoreRelativeOrAbsolutePath: String =
                    Arrays.stream(scorePath.split(File.pathSeparator).toTypedArray())
                        .filter { path -> FileResource(File(path)).contains(grainPartSource) }
                        .findFirst().get()
                val scoreDir = File(scoreRelativeOrAbsolutePath)
                scoreDir.getAbsolutePath()
            }
            value.forEach(
                Consumer<GrainElement> { ge: GrainElement? -> generator.generateCursor(ge, sp) }
            )
        }
    }

    private fun sourceRoot(): File {
        return File(
            project.layout.buildDirectory.get().asFile.absolutePath
                    + File.separator.toString()
                    + "generated-sources"
                    + File.separator.toString()
        )
    }


}