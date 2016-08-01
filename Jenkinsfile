node {
  stage 'Build and Test'
  env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
  scm checkout
  sh 'mvn clean install -U'
}