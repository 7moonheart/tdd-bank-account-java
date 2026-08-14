pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                git branch: 'main', url: 'https://github.com/7moonheart/tdd-bank-account-java.git'
                bat 'mvn clean verify'
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar, target/surefire-reports/*'
            }
        }
    }
}