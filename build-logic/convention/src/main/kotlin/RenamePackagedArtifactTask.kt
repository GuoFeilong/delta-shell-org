import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class RenamePackagedArtifactTask : DefaultTask() {
    @get:Input
    abstract val sourceDirectoryPaths: ListProperty<String>

    @get:Input
    abstract val artifactExtension: Property<String>

    @get:Input
    abstract val outputFileName: Property<String>

    @TaskAction
    fun rename() {
        val sourceDir = sourceDirectoryPaths.get()
            .map(::File)
            .firstOrNull { it.isDirectory }
            ?: return

        val extension = artifactExtension.get()
        val newName = outputFileName.get()

        sourceDir.listFiles { file ->
            file.isFile && file.extension.equals(extension, ignoreCase = true)
        }?.forEach { artifact ->
            if (artifact.name == newName) return@forEach
            val target = File(sourceDir, newName)
            if (target.exists()) {
                target.delete()
            }
            check(artifact.renameTo(target)) {
                "Failed to rename ${artifact.name} to $newName"
            }
        }
    }
}
