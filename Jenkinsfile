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
                // 只打包，不运行任何测试（测试将在并行阶段执行）
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Start Service') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            steps {
                bat 'mvn spring-boot:start'
            }
        }
        // 新增：并行执行演示
        stage('Parallel Tasks') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            parallel {
                stage('Task A: Run Unit Tests') { // 每个并行任务都是一个独立的stage，有自己的步骤
                    steps {
                        bat 'echo "=== 运行单元测试 ==="'
                        bat 'mvn test -Dtest=AccountTest, SortedAccountTest, AccountNotificationTest'
                    }
                }
                stage('Task B: Run API Tests') {
                    steps {
                        bat 'echo "=== 运行 API 测试（Newman） ==="'
                        bat 'newman run Bank_Account_API_Tests.postman_collection.json -e Local.postman_environment.json -r html'
                    }
                }
                // 更多测试类，可用继续添加并行任务
            }
        }
        stage('Stop Service') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            steps {
                bat 'mvn spring-boot:stop -Dspring-boot.stop.force=true'
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