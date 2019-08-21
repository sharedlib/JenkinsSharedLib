def call() {
    
    pipeline {
        agent any
        stages {      
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
                            if("${props['runUnitTestAsGoal']}" == "true") {
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
           // }
            
            stage('sonar code quality'){
                steps {
                    script {
                    def props = readProperties  file:'user.properties'
                     if("${props['runSonarAsGoal']}" == "true") {
                        sh """
                        mvn sonar:sonar \
                       -Dsonar.projectKey="${props['sonarProjectKey']}" \
                       -Dsonar.host.url="${props['sonarUrl']}" \
                       -Dsonar.login="${props['sonarLogin']}" \
                       -Dsonar.projectName="${props['sonarProjectName']}"
                       """ 
                      }
                   }
                }
            }            
            
            stage ('Publish Artifacts') {
                steps {
                     script {
                        def props = readProperties  file:'user.properties'
                            if("${props['runDeployAsGoal']}" == "true") {
                                  sh "mvn ${props['mavenDeploy']}" 
                       }
                   } 
                }
            }
        }
        post {
            always {
               script {
                  def props = readProperties  file:'user.properties'
                  mail(body: "Run ${env.JOB_NAME}-${env.BUILD_NUMBER}-${currentBuild.result}.To get more details, visit the build results page: ${env.BUILD_URL}.",
                       cc: "${props['ccEmail']}",
                       subject: "Job Name: ${env.JOB_NAME}, Build Number: ${env.BUILD_NUMBER}, Build Status: ${currentBuild.result}",
                       to: "${props['toEmail']}")
             }
           }
        }
    }
}
