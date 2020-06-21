FROM maven:alpine

MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

 COPY tesuto-content-api/target/tesuto-content-api.jar tesuto-content-api.jar
 COPY tesuto-content-core/target/tesuto-content-core.jar tesuto-content-core.jar
 COPY tesuto-content-dao-mongo/target/tesuto-content-dao-mongo.jar tesuto-content-dao-mongo.jar
 COPY tesuto-content-domain/target/tesuto-content-domain.jar tesuto-content-domain.jar
 COPY tesuto-content-microservice/target/tesuto-content-microservice.jar tesuto-content-microservice.jar

EXPOSE 8080 \
8444 \
10000 \
10001 \
8000 \
8004 \
8441 \
1099 \
8084

# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-content-microservice.jar"]



