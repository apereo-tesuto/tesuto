FROM maven:alpine

MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

COPY tesuto-admin-api/target/tesuto-activation-api.jar tesuto-activation-api.jar
COPY tesuto-admin-core/target/tesuto-admin-core.jar tesuto-admin-core.jar
COPY tesuto-admin-domain/target/tesuto-admin-domain.jar tesuto-admin-domain.jar
COPY tesuto-admin-microservice/target/tesuto-admin-microservice.jar tesuto-admin-microservice.jar

EXPOSE 8080 \
8442 \
10000 \
10001 \
8000 \
8002 \
8441 \
1099 \
8452 \
8082

# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-admin-microservice.jar"]



