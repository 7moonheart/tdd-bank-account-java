pipeline {
    agent any

    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: '要构建的分支名')
        choice(name: 'RUN_TESTS', choices: ['yes', 'no'], description: '是否运行测试？')
    }

    environment {
        APP_VERSION = '1.0.0'
        BUILD_TIME = new Date().format('yyyy-MM-dd HH:mm:ss')
    }

    stages {
        stage('Build') {
            steps {
                bat 'echo 当前分支: %BRANCH%'
                bat 'echo 当前版本: %APP_VERSION%'
                bat 'echo 构建时间: %BUILD_TIME%'
                bat 'mvn clean verify -Dspring-boot.stop.force=true'
            }
        }

        // 新增的测试阶段，受条件控制
        stage('Run Tests') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            steps {
                bat 'echo 开始运行测试...'
                // 这里可以执行具体的测试命令，比如只运行单元测试
                bat 'mvn test'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar, target/surefire-reports/*'
            }
        }
    }

    post {
        failure {
            echo '❌ 构建失败！请检查代码变更或依赖问题。'
        }
        success {
            echo '✅ 构建成功！'
        }
    }
}