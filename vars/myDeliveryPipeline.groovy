
def call(Map pipelineParams) {

    pipeline {
        agent any
        stages {
            stage('checkout git') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                }
            }

          stage('properties') {
                steps {
                    script {
                          def props = readProperties  file:'user.properties'
                          def Var1= props['sonarprojectKey']
                          echo "${Var1}"
                    }
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
                    def props = readProperties  file:'user.properties'
                    def Var1= props['sonarprojectKey']
                    sh """mvn sonar:sonar \
                   -Dsonar.projectKey= props['sonarprojectKey'] \
                   -Dsonar.host.url='${pipelineParams.hostUrl}' \
                   -Dsonar.login='${pipelineParams.sonarLogin}'""" 
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
                mail to: pipelineParams.email, subject: 'Pipeline failed', body: "${env.BUILD_URL}"
            }
        }
    }
}
