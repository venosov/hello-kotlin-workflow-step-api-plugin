# hello-kotlin-workflow-step-api-plugin
See https://github.com/jenkinsci/workflow-step-api-plugin

    pipeline {
        agent any

        stages {
            stage('build') {
                steps {
                    hellostepk()
                }
            }
        }
    }

