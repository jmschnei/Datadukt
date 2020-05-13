FROM maven:3.6-jdk-11-slim

MAINTAINER Julian Moreno Schneider <julian.moreno_schneider@dfki.de>

RUN mkdir /var/maven/ && chmod -R 777 /var/maven
RUN mkdir mkdir /tmp/workflowmanager/ && chmod -R 777 /tmp/workflowmanager
ENV MAVEN_CONFIG /var/maven/.m2

ADD pom.xml /tmp/workflowmanager
COPY lib /tmp/workflowmanager/lib
RUN cd /tmp/workflowmanager && mvn -B -e -C -T 1C -Duser.home=/var/maven org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline 

RUN chmod -R 777 /var/maven
EXPOSE 8088

COPY . /tmp/workflowmanager
COPY src/main/resources/application_server.properties /tmp/workflowmanager/src/main/resources/application.properties
WORKDIR /tmp/workflowmanager
RUN mvn -Duser.home=/var/maven clean install -DskipTests

RUN chmod -R 777 /tmp/workflowmanager

CMD mvn -Duser.home=/var/maven spring-boot:run
#CMD ["/bin/bash"]
