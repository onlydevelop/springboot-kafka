pipeline {
    stages {
        stage('Checkout') {
            steps { //Checking out the repo
                checkout
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