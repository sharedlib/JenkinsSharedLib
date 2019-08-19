def call() {
def props
    
    pipeline {
        agent any
        stages {      
            stage('checkout git') {
                steps {
                    //git branch: pipelineParams.branch, url: pipelineParams.scmUrl 
                    script {
                        props = readProperties  file:'user.properties'    
                    }   
                        //git branch="${props['branch']}" \
                        //url="${props['scmUrl']} 
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
                    //props = readProperties  file:'user.properties'
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
