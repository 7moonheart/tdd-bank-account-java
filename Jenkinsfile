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

        // 新增：并行执行演示
        stage('Parallel Tasks') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            parallel {
                stage('Task A: Run AccountTest') { // 每个并行任务都是一个独立的stage，有自己的步骤
                    steps {
                        bat 'echo "运行 AccountTest 单元测试..."'
                        bat 'mvn test -Dtest=AccountTest'Windows 下等待 3 秒
                    }
                }
                stage('Task B: Run SortedAccountTest') {
                    steps {
                        bat 'echo "运行 SortedAccountTest 单元测试..."'
                        bat 'mvn test -Dtest=SortedAccountTest'
                    }
                }
                // 更多测试类，可用继续添加并行任务
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