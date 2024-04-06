@file:Suppress("UnstableApiUsage")

package win.german.noob.gleam

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspFormattingSupport
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem


object GleamFileType: LanguageFileType(GleamLanguage) {
    override fun getName() = "Gleam File"
    override fun getDescription() = "Gleam language file"
    override fun getDefaultExtension() = "gleam"
    override fun getIcon() = GleamIcons.FILE
}

object GleamLanguage : Language("Gleam")

object GleamIcons {
    val FILE = getIcon("/icons/lucy.svg", GleamIcons::class.java)
    val DEBUG = getIcon("/icons/lucydebugfail.svg", GleamIcons::class.java);
}

internal class GleamLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.extension == "gleam") {
            serverStarter.ensureServerStarted(FooLspServerDescriptor(project))
        }
    }

    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        LspServerWidgetItem(lspServer, currentFile, GleamIcons.DEBUG, null)
}

private class FooLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "Gleam") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "gleam"
    override fun createCommandLine() = GeneralCommandLine("gleam", "lsp")
    override val lspFormattingSupport = object: LspFormattingSupport() {
        override fun shouldFormatThisFileExclusivelyByServer(
            file: VirtualFile,
            ideCanFormatThisFileItself: Boolean,
            serverExplicitlyWantsToFormatThisFile: Boolean
        ) = serverExplicitlyWantsToFormatThisFile || !ideCanFormatThisFileItself || super.shouldFormatThisFileExclusivelyByServer(
            file,
            ideCanFormatThisFileItself,
            serverExplicitlyWantsToFormatThisFile
        )
    }
}