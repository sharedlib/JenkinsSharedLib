def call() {
    
    pipeline {
        agent any
        stages {      
            stage('checkout git') {
                steps {
                    script {
                        def props = readProperties  file:'user.properties'
                        sh """
                        git branch="${props['branch']}" \
                        url="${props['scmUrl']}" 
                        """
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
                    script {
                    def props = readProperties  file:'user.properties'
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
                mail to: 'adsurenikhil89@gmail.com', subject: 'Pipeline Build Status', body: "${env.BUILD_URL}"
            }
        }
    }
}
