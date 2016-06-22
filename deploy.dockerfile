FROM ingensi/oracle-jdk

RUN yum update -y && yum install -y unzip

RUN mkdir /app

ARG APP_NAME=ilogos-course-generator-ui-1.0-BETA

ADD ./target/universal/${APP_NAME}.zip /app/
RUN unzip /app/${APP_NAME}.zip -d /app/
RUN rm /app/${APP_NAME}.zip
RUN chmod a+x /app/${APP_NAME}/

# LibreOffice installation
RUN \
	yum install -y libreoffice \
	&& yum install -y libreoffice-headless
# TrueType fonts installation 
RUN wget -P /tmp http://www.itzgeek.com/msttcore-fonts-2.0-3.noarch.rpm \
	&& yum localinstall -y /tmp/msttcore-fonts-2.0-3.noarch.rpm
ARG SOFFICE=/usr/bin/soffice

WORKDIR /app/${APP_NAME}/bin
RUN echo '#!/bin/bash' > /app/${APP_NAME}/bin/run
RUN echo '/app/'${APP_NAME}'/bin/ilogos-course-generator-ui "-Dconfig.file=/app/'${APP_NAME}'/conf/${APP_CONFIG:-application.prod.conf}" "-DSOFFICE='${SOFFICE}'" "-Dhttp.port=${APP_PORT}"' >> /app/${APP_NAME}/bin/run
CMD ["sh","run"]