def skipRemainingStages = false
pipeline {
  agent any
  environment {
    CHAT_ID = credentials('chatid')
    TG_TOKEN = credentials('tg_token')
    INTIP = credentials('dev-int-ip')
    USR = credentials('dev-user')
  }
  stages {
    stage('Build') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          script {
            rc = sh(script: "./build.sh", returnStatus: true)
            sh "echo \"exit code is : ${rc}\""
            if (rc != 0)
            {
                sh "echo 'exit code is NOT zero'"
                skipRemainingStages = true
            }
            else
            {
                sh "echo 'exit code is zero'"
            }
          }
        }
      }
    }
    stage('Test') {
      when {
                expression { !skipRemainingStages }
      }
      steps {
        echo 'Testing..'
      }
    }
    stage('Deploy') {
      when {
                expression { !skipRemainingStages }
      }
      steps {
        withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')]) {
          script {
            rc = sh(script: "./deploy.sh", returnStatus: true)
            sh "echo \"exit code is : ${rc}\""
            if (rc != 0)
            {
                sh "echo 'exit code is NOT zero'"
                skipRemainingStages = true
            }
            else
            {
                sh "echo 'exit code is zero'"
            }
          }
        }
      }
    }
  }
}
