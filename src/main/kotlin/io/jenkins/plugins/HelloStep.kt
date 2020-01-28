package io.jenkins.plugins

import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import jenkins.YesNoMaybe
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.jenkinsci.plugins.workflow.steps.*
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import java.io.IOException
import java.util.*


class HelloStep
@DataBoundConstructor constructor() : Step() {
    @Throws(Exception::class)
    override fun start(context: StepContext): StepExecution {
        return Execution(context)
    }

    private class Execution(context: StepContext): SynchronousNonBlockingStepExecution<Void>(context) {
        fun foo(): Flow<Int> = flow { // flow builder
            for (i in 1..3) {
                delay(100) // pretend we are doing something useful here
                emit(i) // emit next value
            }
        }

        @InternalCoroutinesApi
        override fun run(): Void? {
            val listener = context.get(TaskListener::class.java)!!
            val root = context.get(FilePath::class.java)!!

            var filesNumber = 0
            var directoriesNumber = 0
            val toProcess: Stack<FilePath> = Stack()
            toProcess.push(root)

            while (!toProcess.isEmpty()) {
                val path: FilePath = toProcess.pop()
                if (path.isDirectory) {
                    toProcess.addAll(path.list())
                    directoriesNumber++
                } else {
                    filesNumber++
                }
            }

            listener.logger.println("files number: $filesNumber --- directories number: $directoriesNumber")

            runBlocking {
                foo().collect { value -> listener.logger.println("testing flow $value") }
            }

            return null
        }
    }

    @Extension(dynamicLoadable = YesNoMaybe.YES, optional = true)
    class DescriptorImpl : StepDescriptor() {
        override fun getDisplayName(): String {
            return "hellok"
        }

        override fun getFunctionName(): String {
            return "hellostepk"
        }

        override fun takesImplicitBlockArgument(): Boolean {
            return true
        }

        override fun getHelpFile(): String {
            return "$descriptorFullUrl/help"
        }

        @Throws(IOException::class)
        override fun doHelp(request: StaplerRequest, response: StaplerResponse) {
            response.contentType = "text/html;charset=UTF-8"
            val writer = response.writer
            writer.println("descriptionv")
            writer.flush()
        }

        override fun getRequiredContext(): Set<Class<*>?> {
            return setOf(TaskListener::class.java)
        }
    }
}
