def call(Map configMap){
    // mapName.get("key-name")
    def component = configMap.get("component")
    echo "component is : $component"
    pipeline {
        agent { node { label 'AGENT-1'} }
        environment{
            //here if you create any variable you'll have global access, since it is env no need to define
            packageVersion = ''
            ACCOUNT_ID "3434"
            REGION = "us-east-1"
        }

        stages {
            stage('Get Version'){
                steps{
                    script{
                        def packageJson = readJson(file: 'package.json')
                        packageVersion = packageJson.version 
                        echo "version: {packageVersion}"
                    }
                }
            }
            stage('Install dependencies') {
                steps {
                    sh 'npm install'
                }
            }
            stage('Unit test') {
                steps {
                    echo "unit testing is done here"
                }
            }
            //sonar-scanner command expect sonar-project.properties should be available
            stage('Sonar Scan') {
                steps {
                    echo "Sonar scan done"
                }
            }
            stage('Build') {
                steps {
                    sh 'ls -ltr'
                    sh "zip -r ${component}.zip ./* --exclude=.git --exclude=.zip"
                }
            }
            stage('SAST') {
                steps {
                    echo "SAST done"
                    echo "package version: $packageVersion"
                }
            }
            // install pipeline utility steps plugins, if not installed 
            stage('Publish Artifact') {
                steps {
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3', 
                        protocol: 'http',
                        nexusUrl: '10.0.0.0/',
                        groupId: 'com.roboshop',
                        version: "$packageVersion",
                        repository: "${component}",
                        credentialsId: 'nexus-auth',
                        artifacts: [
                            [artifactsId: "${component}",
                            classifier: '',
                            file: "${component}.zip",
                            type: 'zip'
                            ]
                        ]
                    )
                }
            }

            stage('Docker Build') {
                steps {
                    script{
                        sh """
                            docker build -t joindevops/${component}:${packageVersion} .
                        """
                    }
                }
            }
        // just make sure you login in inside agent 
            stage('Docker Push') {
                steps {
                    script{
                        withCredentials([usernamePassword(credentialsId: 'docker-auth', usernameVariable: 'USERNAE', passwordVariable: 'PASSWORD')]) {
                            echo USERNAME
                            echo "username is $USERNAME"
                            sh """
                                docker login -u $USERNAME -p $PASSWORD
                                docker push joindevops/${component}:${packageVersion}
                            """
                        }
                    }
                }
            }

            stage('ECR Push'){
                steps{
                    script{
                        sh """
                            aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com
                            docker tag joindevops/${component}:${packageVersion} ${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${component}:${packageVersion}
                            docker push ${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${component}:${packageVersion}
                        """
                    }
                }
            }

            stage('EKS Deploy') {
                steps {
                    script{
                        sh """
                            cd helm 
                            sed -i 's/IAMGE_VERSION/$packageVersion/g' values.yaml
                            helm upgrade ${component} -n roboshop .
                        """ 
                    }
                }
            }

            // here I need to configuration downstram job. I have to pass package version for deployment
            // This job will wait until downstream job is over
            // by default when a non-master branch CI is done, we can go for DEV deployment
            stage('Deploy') {
                steps {
                    script{
                        echo "Deployment" 
                        def params = [
                            string(name: 'version', value: "$packageVersion"),
                            string(name: 'environment', value: "dev")
                        ]
                        build job: "../${component}-deploy", wait: true, parameters: params
                    }
                }
            }
        }
        post{
            always{
                echo 'cleaning up workspace'
                //deleteDir()
            }
        }
    }
}
        