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
            stage ('package') {
                steps {
                    sh 'mvn package'
                }
            }
            
            stage('sonar code quality'){
                steps {
                    sh """mvn sonar:sonar \
                   -Dsonar.projectKey='${projectKey}' \
                   -Dsonar.host.url='http://35.184.178.95:9000' \
                   -Dsonar.login='7aa0f5eec8f0ef469314e6b95f4c18ce822b0891'"""
                   
                    
                 
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
