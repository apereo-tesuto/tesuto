FROM maven:alpine
#FROM docker.dev.ccctechcenter.org/base-newrelic:1.2.0

FROM docker.dev.ccctechcenter.org/base-newrelic:1.3.0
MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

COPY target/tesuto-ui.jar tesuto-ui.jar
#COPY target/lib/* ./
COPY src/main/resources/metadata/assess-localhost-metadata.xml assess-localhost-metadata.xml
#RUN rm tesuto-spring-boot-2.2.0-SNAPSHOT.jar

EXPOSE 8080 \
8451 \
10000 \
10001 \
8000 \
8011 \
8441 \
1099 \
8444 \
8092 \
8452


# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

<<<<<<< HEAD
CMD ["/bin/bash", "/runService.sh"]
=======
#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-ui.jar", "--saml.metadata.provider.classpath=/BOOT-INF/classes/metadata/assess-localhost-metadata.xml"]



>>>>>>> refactor-msb-sheraz
