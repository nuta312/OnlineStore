pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['ALL', 'API', 'E2E', 'REGRESSION', 'SMOKE', 'UI', 'DB'],
            description: 'Выбери какие тесты запустить'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    if (params.TEST_SUITE == 'ALL') {
                        bat 'gradlew.bat clean test || exit 0'
                    } else if (params.TEST_SUITE == 'API') {
                        bat 'gradlew.bat clean apiTest || exit 0'
                    } else if (params.TEST_SUITE == 'E2E') {
                        bat 'gradlew.bat clean e2eTest || exit 0'
                    } else if (params.TEST_SUITE == 'REGRESSION') {
                        bat 'gradlew.bat clean regressionTest || exit 0'
                    } else if (params.TEST_SUITE == 'SMOKE') {
                        bat 'gradlew.bat clean smokeTest || exit 0'
                    } else if (params.TEST_SUITE == 'UI') {
                        bat 'gradlew.bat clean uiTest || exit 0'
                    } else if (params.TEST_SUITE == 'DB') {
                        bat 'gradlew.bat clean dbTest || exit 0'
                    }
                }
            }
        }

        stage('Allure Report') {
            steps {
                allure([
                    results: [[path: 'build/allure-results']]
                ])
            }
        }

        stage('Telegram Notification') {
            steps {
                bat "java -DconfigFile=notifications/config.json -jar notifications/allure-notifications-4.6.1.jar"
            }
        }
    }

    post {
        always {
            allure([
                results: [[path: 'build/allure-results']]
            ])
        }
        success {
            echo 'Тесты прошли успешно!'
        }
        failure {
            echo 'Есть упавшие тесты!'
        }
    }
}