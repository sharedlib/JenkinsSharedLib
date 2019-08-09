
def call(Map pipelineParams) {

    pipeline {
        agent any
        stages {
            stage('checkout git') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
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
            
             stage ('properties') {
                steps {
                    Properties properties = new Properties()
                    File propertiesFile = new File('${WORKSPACE}/user.properties')
                    propertiesFile.withInputStream {
                    properties.load(it)
                    sh 'echo "Accessing perperties"'
                    sh 'echo ${properties.a}'
                    }
                }
            }
            
            stage ('package') {
                steps {
                    sh 'mvn package'
                }
            }
            
            stage('sonar code quality'){
                steps {
                    sh """mvn sonar:sonar \
                   -Dsonar.projectKey='${pipelineParams.projectKey}' \
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
