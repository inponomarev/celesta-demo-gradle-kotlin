import org.apache.maven.model.Resource
import org.apache.maven.plugin.MojoExecutionException
import org.gradle.api.Project
import ru.curs.celesta.CelestaException
import ru.curs.celesta.score.Grain
import ru.curs.celesta.score.GrainPart
import ru.curs.celesta.score.Namespace
import ru.curs.celesta.score.Score
import ru.curs.celesta.score.io.FileResource
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Collectors

class ResourcesGenerator(
    private val project: Project
) {
    fun execute() {
        val grainsSources: MutableList<GrainSourceBag> = ArrayList()
        val scorePath: String =
            project.layout.projectDirectory.asFile.absolutePath.toString() +
                    File.separator.toString() +
                    "src/main/celestasql"
        val scoreResources: List<FileResource> = Arrays.stream(scorePath.split(File.pathSeparator).toTypedArray())
            .map { pathname: String ->
                File(pathname)
            }
            .map { it.absoluteFile }
            .map(::FileResource)
            .collect(Collectors.toList())
        val score: Score = initScore(scorePath)
        score.grains.values.stream()
            .filter(::isAllowGrain)
            .flatMap { g: Grain ->
                g.grainParts.stream()
            }
            .forEach { gp: GrainPart ->
                if (gp.source != null) {
                    if ((Namespace.DEFAULT == gp.namespace)) {
                        throw CelestaException(
                            "Couldn't generate score resource for %s without package",
                            gp.source
                        )
                    }
                    val scoreSource: FileResource = scoreResources.stream()
                        .filter { it.contains(gp.source) }
                        .findFirst().get()
                    grainsSources.add(
                        GrainSourceBag(
                            scoreSource,
                            gp.source
                        )
                    )
                }
            }

        if (grainsSources.isEmpty()) {
            return
        }
        copyGrainSourceFilesToResources(grainsSources)
        generateScoreFiles(grainsSources)
        val scoreResource = Resource()
        scoreResource.directory = resourcesRoot().absolutePath
        scoreResource.targetPath = "score"
    }

    private fun copyGrainSourceFilesToResources(
        grainSources: Collection<GrainSourceBag>
    ) {
        val resourcesRootPath: Path = resourcesRoot().toPath()
        val tos: MutableSet<Path> = HashSet()
        for (gs: GrainSourceBag in grainSources) {
            val to: Path = gs.resolve(resourcesRootPath)
            if (!tos.add(to)) {
                throw MojoExecutionException(
                    String.format(
                        "There are more than one grain source files being copied to %s",
                        to
                    )
                )
            }
            try {
                val toParent: Path? = to.parent
                if (toParent != null) {
                    Files.createDirectories(toParent)
                }
                Files.copy(gs.grainSource.inputStream, to, StandardCopyOption.REPLACE_EXISTING)
            } catch (ex: IOException) {
                throw MojoExecutionException(
                    String.format("Copying of grain source file failed: %s", gs.grainSource),
                    ex
                )
            }
        }
    }

    private fun convertSeparatorChar(path: String): String {
        return if (File.separatorChar != '/') {
            path.replace(File.separatorChar, '/')
        } else {
            path
        }
    }

    private fun generateScoreFiles(grainsSources: List<GrainSourceBag>) {
        val relativeSourcesPaths: Collection<String> = grainsSources.stream()
            .map { it.grainSourceRelativePath.toString() }
            .map { convertSeparatorChar(it) }
            .collect(Collectors.toCollection { TreeSet() })
        val scoreFilesPath: Path = File(resourcesRoot(), SCORE_FILES_FILE_NAME).toPath()
        try {
            Files.write(scoreFilesPath, relativeSourcesPaths)
        } catch (ex: IOException) {
            throw MojoExecutionException("Error writing a score.files", ex)
        }
    }

    private class GrainSourceBag(
        val scoreSource: ru.curs.celesta.score.io.Resource,
        val grainSource: ru.curs.celesta.score.io.Resource
    ) {
        fun resolve(rootPath: Path): Path {
            return rootPath.resolve(grainSourceRelativePath)
        }

        val grainSourceRelativePath: Path
            get() {
                return File(scoreSource.getRelativePath(grainSource)).toPath()
            }

    }

    private fun resourcesRoot(): File {
        return File(
            project.layout.projectDirectory.asFile.absolutePath.toString()
                    + File.separator + "build/generated-resources" + File.separator + "score"
        )
    }
}

const val SCORE_FILES_FILE_NAME: String = "score.files"