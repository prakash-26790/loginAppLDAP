FROM tomcat:8

#RUN apt-get update && apt-get install tomcat8 -y

COPY target/auth-1.3.5.RELEASE.war webapps/

#CMD ["/usr/share/tomcat8/bin/catalina.sh", "run"]

