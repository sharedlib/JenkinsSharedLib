 properties = NULL
    
    def loadProperties(){
        //checkout scm
     File propertiesFile = new File('/var/lib/jenkins/workspace/My_GitHub_Org_Audi_master/user.properties')
        propertiesFile.withInputStream{
            properties.load(propertiesFile)
        }
    }

def call() {
    
    pipeline {
        agent any
        stages {      
            stage('checkout git') {
                steps {
                    //git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                    echo "checkout scm"
                }
            }
         
            stage('Clean Lifcycle') {
                steps {
                 script {
                    def props = readProperties  file:'user.properties'
                    sh "mvn ${props['mavenClean']}" 
                  }    
                }
            }        
            stage('Build Lifecycle') {
                steps {
                     script {
                        def props = readProperties  file:'user.properties'
                        sh "mvn ${props['mavenCompile']}"
                   }              
                }
            }
         
            stage ('Unit Test') {
                steps {
                     script {
                        def props = readProperties  file:'user.properties'
                         //def test="${props['runUnitTestAsGoal']}"
                         test=false
                         echo "Unittest=$test"
                            if ($test == true) {
                                sh "mvn ${props['mavenTest']}"
                   } 
                }
            }
         }
            stage ('Package Creation') {
                steps {
                     script {
                        def props = readProperties  file:'user.properties'
                        sh "mvn ${props['mavenPackage']}"
                   } 
                }
            }
            
            stage('sonar code quality'){
                steps {
                    script {
                    def props = readProperties  file:'user.properties'
                    //loadProperties()
                    sh """
                    mvn sonar:sonar \
                   -Dsonar.projectKey="${props['sonarProjectKey']}" \
                   -Dsonar.host.url="${props['sonarUrl']}" \
                   -Dsonar.login="${props['sonarLogin']}"
                   """ 
                    }
                }
            }            
            
            stage('Publish Artifacts'){
                steps {
                     script {
                        def props = readProperties  file:'user.properties'
                        sh "mvn ${props['mavenDeploy']}"
                   } 
                }
            }
        }
        post {
            always {
               script {
                  def props = readProperties  file:'user.properties'
                  mail to: "${props['toEmail']}", subject: 'Pipeline Build Status', body: "${env.BUILD_URL}"
             }
           }
        }
    }
}
