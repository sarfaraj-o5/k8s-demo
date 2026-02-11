def call(Map configMap){
    // mapName.get("key-name")
    def component = configMap.get("component")
    echo "component is : $component"
    pipeline {
        agent {node { label 'AGENT-1' } }
        environment{
            // here if you create any vars you will have global access, since its env no need to def
            packageVersion = ''
        }

        stages {
            stage('Get Version'){
                steps{
                    script{
                        def packageJson = readJSON(file: 'package.json')
                        packageVersion = packageJson.version
                        echo "version: ${packageVersion}"
                    }
                }
            }

            stage('Install dependencies') {
                steps{
                    sh 'npm install'
                }
            }
            stage('Unit test'){
                steps {
                    echo "unit testing is done here"
                }
            }
            //sonar-scanner cmd expect sonar-project.properties should be available
            stage('Sonar Scan'){
                steps{
                    echo "Sonar scan done"
                }
            }
            stage('Build') {
                steps{
                    sh "ls -ltr"
                    sh "zip -r ${component}.zip ./* --exclude=.git --exclude=.zip"
                }
            }
            stage('SAST'){
                steps{
                    echo "SAST done"
                    echo "package version: ${packageVersion}"
                }
            }
            //install pipeline utility steps plugins, if not installed
            stage('Publish Artifact'){
                steps{
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '172.x.x.x:8081/',
                        groupId: "com.roboshop",
                        version: "$packageversion",
                        repository: "${component}",
                        credentialsId: 'nexus-auth',
                        artifacts: [
                            [artifactId: "${component}",
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
            // just make sure you login inside agent
            stage('Docker Push') {
                steps{
                    script{
                        withCredentials([usernamePassword(credentialsId: 'docker-auth', usernameVariable: 'USERNAME', PasswordVariable: 'PASSWORD')]) {
                            // available as an env var, but will be masked if you try to print it out any which way
                            // note: single quotes prevent Groovy interpolation; expansion is by Bourne Shell, which is what you want
                            // also available as Groovy var
                            // or inside double quotes for string interpolation
                            echo "usename is $USERNAME"
                            sh """
                                docker login -u $USERNAME -p $PASSWORD
                                docker push joindevops/${component}:${packageVersion}
                            """
                        }
                    }
                }
            }

            stage('EKS Deploy') {
            steps {
                script{
                sh """
                    cd helm
                    sed -i 's/IMAGE_VERSION/$packageVersion/g' values.yaml
                    helm install ${component} -n roboshop .
                """
                }
            }
        }

        // here I need to configure downstream job. I have to pass package version for deployment 
        // This job will wait until downstream job is over
        // by default whan a non-master branch CI is done, we can go for DEV development
        // stage('Deply') {
            //     steps{
            //         script{
            //             echo "Deployment"
            //             def params = [
            //                 string(name: 'version', value: "$packageVersion"),
            //                 string(name: 'environment', value: "dev")
            //             ]
            //             build job: "../${component}-deploy", wait: true, parameters: params
            //         }
            //     }
            // }

        }
        
        post{
            always{
                echo 'cleaning up workspace'
                //deleteDir()
            }
        }
    }
}