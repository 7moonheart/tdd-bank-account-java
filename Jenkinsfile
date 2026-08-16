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
        stage('Prepare Git') {
            steps {
                bat 'git config --global http.version HTTP/1.1'
                bat 'echo 已设置 Git 使用 HTTP/1.1 协议'
            }
        }

        stage('Checkout SCM') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    checkout([
                            $class: 'GitSCM',
                            branches: [[name: '*/main']],
                            extensions: [
                                [$class: 'CloneOption', depth: 1, noTags: false, reference: ''],
//                                [$class: 'TimeoutOption', timeout: 10]
                            ],
                            userRemoteConfigs: [[url: 'https://github.com/7moonheart/tdd-bank-account-java.git']]
                        ])
                }
            }
        }

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
            steps {
                bat 'mvn spring-boot:start'
            }
        }
        // 并行执行
        stage('Parallel Tasks') {
            when {
                expression { params.RUN_TESTS == 'yes' }
            }
            parallel {
                // 每个并行任务都是一个独立的stage，有自己的步骤
                stage('Task A: Java Unit Tests') {
                    steps {
                        bat 'echo "=== 运行单元测试 ==="'
//                        bat 'mvn test -Dtest=AccountTest,SortedAccountTest,AccountNotificationTest' // 逗号后面有空格会不通过
                        bat 'mvn test -Dtest=*Test' // 用通配符匹配所有测试类
                    }
                }
                stage('Task B: Newman API Tests') {
                    steps {
                        bat 'echo "=== 运行 API 测试（Newman） ==="'
                        bat 'newman run Bank_Account_API_Tests.postman_collection.json -e Local.postman_environment.json -r html'
                    }
                }
                stage('Task C: pytest API Tests') {
                    steps {
                        bat '''
                            cd py_tests
                            venv\\Scripts\\activate && pytest test_bank_api.py -v --html=report.html
                        '''
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'py_tests/report.html'
                        }
                    }
                }
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
    }

    post {
        failure {
            echo '❌ 构建失败！请检查代码变更或依赖问题。'
        }
        success {
            echo '✅ 构建成功！'
        }
        always {
            archiveArtifacts artifacts: 'target/*.jar, target/surefire-reports/*, newman/*, py_tests/report.html'
        }
    }
}