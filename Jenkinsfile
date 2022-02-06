pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git branch: 'citest',
                    url: 'https://github.com/onlydevelop/springboot-kafka.git'
            }
        }
        stage('filestore') {
            steps {
                script {    
                    try {
                        sh 'cd services/filestore && ./gradlew clean build --no-daemon'
                    } finally {
                        //junit '**/build/test-results/test/*.xml'
                    }
                }
            }
        }
        stage('parser') {
            steps {
                script {    
                    try {
                        sh 'cd services/parser && ./gradlew clean build --no-daemon'
                    } finally {
                        //junit '**/build/test-results/test/*.xml'
                    }
                }
            }
        }
        stage('uploader') {
            steps {
                script {    
                    try {
                        sh 'cd services/uploader && ./gradlew clean build --no-daemon'
                    } finally {
                        //junit '**/build/test-results/test/*.xml'
                    }
                }
            }
        }
        // stage('Build') {   
        //     parallel {
        //         stage('filestore') {
        //             agent any
        //             steps {
        //                 script {    
        //                     try {
        //                         sh 'cd services/filestore && ./gradlew clean build --no-daemon'
        //                     } finally {
        //                         //junit '**/build/test-results/test/*.xml'
        //                     }
        //                 }
        //             }
        //         }
        //         stage('parser') {
        //             agent any
        //             steps {
        //                 script {    
        //                     try {
        //                         sh 'cd services/parser && ./gradlew clean build --no-daemon'
        //                     } finally {
        //                         //junit '**/build/test-results/test/*.xml'
        //                     }
        //                 }
        //             }
        //         }
        //         stage('uploader') {
        //             agent any
        //             steps {
        //                 script {    
        //                     try {
        //                         sh 'cd services/uploader && ./gradlew clean build --no-daemon'
        //                     } finally {
        //                         //junit '**/build/test-results/test/*.xml'
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
    }
}