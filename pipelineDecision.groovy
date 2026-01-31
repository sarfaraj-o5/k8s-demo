#!groovy

def decidePipeline(Map configMap){
    application = configMap.get("applilcation")
    //# here we are getting nodeJSVM 
    switch(application) {
        case 'nodeJSVM':
            echo "application is Node JS and VM basesd"
            nodeJSVMCI(configMap)
            break
        case 'nodeJSEKS':
            echo "application is NodeJS and VM based"
            nodeJSEKS(configMap)
            break
        case 'JavaVM':
            JavaVMCI(configMap)
            break
        case 'javaEKS':
            javaEKS(configMap)
            break
        default:
            error "Unrecongnised application"
            break
    }
}