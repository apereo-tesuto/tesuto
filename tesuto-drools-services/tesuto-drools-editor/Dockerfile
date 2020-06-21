# assess-drools-editor - Brief description
#
# NAMESPACE: docker.dev.ccctechcenter.org
# IMAGE NAME: assess-drools-editor

FROM docker.dev.ccctechcenter.org/base-newrelic:1.5.0
MAINTAINER docker@unicon.net

ENV PORTAL_HOME=/opt/ccc/config \
    M2_HOME=/usr/share/maven \
    PATH=/usr/share/maven/bin/:$PATH \
    MAVEN_VERSION=3.3.9

WORKDIR /tmp

RUN  wget \
        --progress=dot:mega \
        http://mirror.reverse.net/pub/apache/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && mkdir /usr/share/maven \
  && tar -zxf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/share/maven --strip-components 1 \
  && rm -rf /tmp/* \
            /var/tmp/*

CMD ["/bin/bash", "/runService.sh"]

#CMD /bin/bash -c while true; do ping 0.0.0.0; done

EXPOSE 8080

COPY target/assess-drools-editor.jar /app.jar
COPY includes/ /
