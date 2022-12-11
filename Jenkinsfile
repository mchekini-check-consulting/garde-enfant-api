node("ci-node") {
    stage("checkout") {
        checkout([$class: 'GitSCM', branches: [[name: '*/develop']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/mchekini-check-consulting/famille-api.git']]])
    }
    stage("build") {
        sh "chmod 777 mvnw"
        sh "./mvnw clean package -DskipTests=true"
    }

//    stage("Quality Analyses"){
//        sh "chmod 777 mvnw"
//        sh "./mvnw clean verify sonar:sonar \\\n" +
//                "  -Dsonar.projectKey=famille-api \\\n" +
//                "  -Dsonar.host.url=http://3.87.90.191:11001 \\\n" +
//                "  -Dsonar.login=sqp_8ed28af6678da0cebb6d6dcd03289ddbd55dce79"
//    }

    stage("build docker image") {
        sh "sudo docker build -t famille-api ."
    }
    stage("push docker image") {
        sh "sudo docker login -u mchekini -p jskabyliE"
        sh "sudo docker tag famille-api mchekini/famille-api:1.0"
        sh "sudo docker push mchekini/famille-api:1.0"
        stash includes: 'docker-compose.yml', name: 'utils'
    }
    node("integration-node") {
        stage("deploy famille api") {
            unstash 'utils'
            try {
                sh "sudo docker stop famille-api"
                sh "sudo docker rm famille-api"
                sh "sudo docker-compose down"
                sh "sudo docker rmi mchekini/famille-api:1.0"
            }catch(Exception e){
                println "No container famille-api running"
            }
            sh "sudo docker-compose up -d"
        }
    }

}
