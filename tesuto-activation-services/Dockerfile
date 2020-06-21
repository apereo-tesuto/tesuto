FROM maven:alpine

MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

 COPY tesuto-activation-api/target/tesuto-activation-api.jar tesuto-activation-api.jar
 COPY tesuto-activation-core/target/tesuto-activation-core.jar tesuto-activation-core.jar
 COPY tesuto-activation-dao-postgresql/target/tesuto-activation-dao-postgresql.jar tesuto-activation-dao-postgresql.jar
 COPY tesuto-activation-microservice/target/tesuto-activation.jar tesuto-activation.jar
# COPY . /usr/local/bin/

EXPOSE 8080 \
8443 \
10000 \
10001 \
8000 \
8001 \
8441 \
1099 \
8081

# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-activation.jar"]



