FROM ingensi/oracle-jdk

RUN yum update -y && yum install -y unzip

RUN mkdir /app

ARG APP_NAME=ilogos-course-generator-ui-1.0-BETA

ADD ./target/universal/${APP_NAME}.zip /app/
RUN unzip /app/${APP_NAME}.zip -d /app/
RUN rm /app/${APP_NAME}.zip
RUN chmod a+x /app/${APP_NAME}/

# Installing LibreOffice
RUN yum install -y tar
RUN cd /tmp
RUN wget http://download.documentfoundation.org/libreoffice/stable/5.1.3/rpm/x86_64/LibreOffice_5.1.3_Linux_x86-64_rpm.tar.gz
RUN tar -xvf LibreOffice_5.1.3_Linux_x86-64_rpm.tar.gz
RUN yum localinstall -y *.rpm
ENV SOFFICE /opt/libreoffice5.1/program

WORKDIR /app/${APP_NAME}/bin
ENV CONF application.docker.conf
RUN echo '#!/bin/bash' > /app/${APP_NAME}/bin/run
RUN echo $'/app/'${APP_NAME}'/bin/ilogos-course-generator-ui "-Dconfig.file=/app/'${APP_NAME}'/conf/$CONF" "-Dhttp.port=$APP_PORT"' >> /app/$APP_NAME/bin/run
CMD ["sh","run"]