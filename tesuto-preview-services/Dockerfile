# User Microservice Image
#
# NAMESPACE: docker.dev.ccctechcenter.org
# IMAGE NAME: ccc-assess-user
# TAG: 1.3.0

FROM docker.dev.ccctechcenter.org/base-newrelic:1.2.0
MAINTAINER Richard Scott Smith <ssmith@unicon.net>

COPY includes/ /

EXPOSE 8089 \
8452 \
10000 \
10001 \
8000 \
1099

# Set the timezone to UTC inside the docker container
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#CMD while true; do sleep 1000; done
CMD java -jar /opt/ccc/tesuto-user.jar --spring.config.location=file:/opt/ccc/config/application.properties
