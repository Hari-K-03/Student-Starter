pipeline{
    agent any
    stages{
        stage('Build and Test'){
            steps{
                bat 'mvn clean test'
            }
        }
        stage('Package'){
            steps{
                bat 'mvn package'
            }
        }
    }
    post{
        always{
            junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
        }
        success{
            archiveArtifacts artifacts: 'course-registration-app/target/*.jar',
            fingerprint:true
        }
    }
}