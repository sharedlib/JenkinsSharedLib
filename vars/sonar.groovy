def sonarScan() {
  sh """mvn sonar:sonar \
  -Dsonar.projectKey=abc_sonar \
  -Dsonar.host.url=http://35.239.219.161:9000 \
  -Dsonar.login=7aa0f5eec8f0ef469314e6b95f4c18ce822b0891"""
}
