package io.jenkins.plugins

import hudson.Extension
import hudson.model.TaskListener
import jenkins.YesNoMaybe
import org.jenkinsci.plugins.workflow.steps.*
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import java.io.IOException


class HelloStep
@DataBoundConstructor constructor() : Step() {
    @Throws(Exception::class)
    override fun start(context: StepContext): StepExecution {
        return Execution(context)
    }

    private class Execution(context: StepContext): SynchronousNonBlockingStepExecution<Void>(context) {
        override fun run(): Void? {
            val listener = context.get(TaskListener::class.java)!!
            listener.logger.println("VVV")
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
