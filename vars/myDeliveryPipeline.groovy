 properties = NULL
    
    def loadProperties(){
        //checkout scm
     File propertiesFile = new File('/var/lib/jenkins/workspace/My_GitHub_Org_Audi_master/user.properties')
        propertiesFile.withInputStream{
            properties.load(propertiesFile)
        }
    }

def call(Map pipelineParams) {
    
    pipeline {
        agent any
        stages {      
            stage('checkout git') {
                steps {
                    //git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                    echo "checkout scm"
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
                    //loadProperties()
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
                mail to: pipelineParams.email, subject: 'Pipeline Build Status', body: "${env.BUILD_URL}"
            }
        }
    }
}
