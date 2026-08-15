pipeline {
    agent any

    environment {
        APP_VERSION = '1.0.0'
        BUILD_TIME = new Date().format('yyyy-MM-dd HH:mm:ss')
    }

    stages {
        stage('Build') {
            steps {
                bat 'echo 当前版本: %APP_VERSION%'
                bat 'echo 构建时间: %BUILD_TIME%'
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