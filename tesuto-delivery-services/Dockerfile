FROM maven:alpine

MAINTAINER Sheraz Ahmed "sahmed@unicon.net"

WORKDIR /usr/local/bin/

 COPY tesuto-delivery-api/target/tesuto-delivery-api.jar tesuto-delivery-api.jar
 COPY tesuto-delivery-core/target/tesuto-delivery-core.jar tesuto-delivery-core.jar
# COPY tesuto-delivery-dao-mongo/target/tesuto-content-dao-mongo.jar tesuto-content-dao-mongo.jar
# COPY tesuto-delivery-dao-postgres/target/tesuto-content-domain.jar tesuto-content-domain.jar
 COPY tesuto-delivery-microservice/target/tesuto-delivery-microservice.jar tesuto-delivery-microservice.jar

EXPOSE 8080 \
8445 \
10000 \
10001 \
8000 \
8005 \
8441 \
1099 \
8085

# Set the timezone to UTC inside the docker container
#ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD ["java", "-jar", "tesuto-delivery-microservice.jar"]



