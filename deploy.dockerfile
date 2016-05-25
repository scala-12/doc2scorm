FROM ingensi/oracle-jdk

RUN yum update -y && yum install -y unzip

RUN mkdir /app

EXPOSE 80
ENV D2S_PORT 80

ARG APP_NAME=ilogos-course-generator-ui-1.0-BETA

ADD ./target/universal/${APP_NAME}.zip /app/
RUN unzip /app/${APP_NAME}.zip -d /app/
RUN rm /app/${APP_NAME}.zip
RUN chmod a+x /app/${APP_NAME}/

WORKDIR /app/${APP_NAME}/bin
ENV CONF application.docker.conf
RUN echo '#!/bin/bash' > /app/${APP_NAME}/bin/run
RUN echo $'/app/'${APP_NAME}'/bin/ilogos-course-generator-ui "-Dconfig.file=/app/'${APP_NAME}'/conf/$CONF" "-Dhttp.port=$D2S_PORT"' >> /app/${APP_NAME}/bin/run
CMD ["sh","run"]