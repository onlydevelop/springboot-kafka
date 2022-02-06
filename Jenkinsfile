pipeline {
    stages {
        stage('Checkout') {
            steps {
                git branch: 'citest',
                    url: 'https://github.com/onlydevelop/springboot-kafka.git'
            }
        }
        stage('Build: filestorage') {   
            steps {
                script {
                    try {
                        cd services/filestorage
                        sh './gradlew clean build --no-daemon' //run a gradle task

                    } finally {
                        //junit '**/build/test-results/test/*.xml' //make the junit test results available in any case (success & failure)
                    }
                }
            }
        }
    }
}