FROM maven:alpine

MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

COPY tesuto-importer-api/target/tesuto-dependencies.jar tesuto-dependencies.jar
COPY tesuto-qti-importer-core/target/tesuto-qti-importer-core.jar tesuto-qti-importer-core.jar
COPY tesuto-qti-importer-microservice/target/tesuto-qti-importer-microservice.jar tesuto-qti-importer-microservice.jar

EXPOSE 8080 \
8448 \
10000 \
10001 \
8000 \
8008 \
8441 \
1099

# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-qti-importer-microservice.jar"]



