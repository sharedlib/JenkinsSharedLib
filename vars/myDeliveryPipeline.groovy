def call(Map pipelineParams) {

    pipeline {
        agent any
        stages {
            def props = readProperties  file:'user.properties'
            stage('checkout git') {
                steps {
                    //git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                            git branch: "${props['branch']}", url: "${props['scmUrl']}"
                }
            }
            stage('build') {
                steps {
                    sh 'mvn clean compile'
                }
            }

            stage ('test') {
                steps {
                    sh 'mvn test'
                }
            }
            
            stage ('package') {
                steps {
                    sh 'mvn package'
                }
            }
            
            stage('sonar code quality'){
                steps {
                    script {
                    sh """
                    mvn sonar:sonar \
                   -Dsonar.projectKey="${props['sonarprojectKey']}" \
                   -Dsonar.host.url="${props['sonarUrl']}" \
                   -Dsonar.login="${props['sonarLogin']}"
                   """ 
                    }
                }
            }            
            
            stage('deploy'){
                steps {
                    sh 'mvn deploy'
                }
            }
        }
        post {
            always {
                mail to: "${props['email']}", subject: 'Pipeline Build Status', body: "${env.BUILD_URL}"
            }
        }
    }
}
