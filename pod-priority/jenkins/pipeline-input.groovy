def LABEL_ID = "yourappname-${UUID.randomUUID().toString()}"
def BRANCH_NAME = "<your branch name>"
def GIT_URL = "<your git url>"
// Start Agent
node(LABEL_ID) {
    stage('Checkout') {
        doCheckout('BRANCH_NAME, GIT_URL')
    }
    stage('Build'){
        ...
    }
    stage('Tests'){
        ...
    }
}

// Kill Agent
//Input step
timeout(time: 15, unit: "MINUTES"){
    input message: 'Do you want to approve the deploy in production?', ok: 'Yes'
}
// Start Agent Again
node(LABEL_ID){
    doCheckout(BRANCH_NAME, GIT_URL)
    stage('Deploy'){
        ...
    }
}

def doCheckout(branchName, gitUrl){
    checkout([$class: 'GitSCM',
        branches: [[name: branchName]],
        doGenerateSubmoduleConfigurations: false,
        extensions:[[$class: 'CloneOption', noTags: true, reference: '', shallow: true]],
        userRemoteConfigs: [[credentialsId: '<your credentials id>', url: gitUrl]]])
}