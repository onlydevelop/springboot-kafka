pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git branch: 'citest',
                    url: 'https://github.com/onlydevelop/springboot-kafka.git'
            }
        }
        stage('Build') {   
            steps {
                step {
                    script {
                        try {
                            sh 'cd services/filestore && ./gradlew clean build --no-daemon'
                        } finally {
                            //junit '**/build/test-results/test/*.xml' //make the junit test results available in any case (success & failure)
                        }
                    }
                }
                
            }
        }
    }
}