package io.jenkins.plugins

import arrow.core.Either
import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import jenkins.YesNoMaybe
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
        fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
                if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
                else Either.Right(1.0 / i)

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

            val value = when(val x = reciprocal(1)) {
                is Either.Left -> "Can't take reciprocal of 0!"
                is Either.Right -> "Got reciprocal: ${x.b}"
            }

            listener.logger.println("testing arrow: $value")

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
