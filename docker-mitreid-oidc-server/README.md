# docker-mitreid-oidc-server

# About
Dockerized version of MITREid OpenID-Connect-Java-Spring-Server

# Description
This container is comprised of an OpenID-Connect-Server and an OpenID Connect Authorization Code Flow web application client running on a Tomcat server.

# Usage
```docker run -itd --name=oidc-srv --net=host -p 8080:8080 immontilla/docker-mitreid-oidc-server```

OpenID-Connect-Java-Spring-Server ➡️ http://localhost:8080/openid-connect-server-webapp/

OpenID Connect Authorization Code Flow web application ➡️ http://localhost:8080/simple-web-app/

# More info
OpenID-Connect-Java-Spring-Server Project ➡️ https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server/

OpenID-Connect-Java-Spring-Server Official Wiki ➡️ https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server/wiki
