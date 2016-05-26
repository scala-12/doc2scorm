FROM ingensi/oracle-jdk

ARG PLAY_VERSION=1.3.7

RUN yum update -y && yum install -y unzip
RUN yum install -y samba-client
RUN yum install -y cifs-utils
RUN curl -O http://downloads.typesafe.com/typesafe-activator/1.3.7/typesafe-activator-${PLAY_VERSION}.zip 
RUN unzip typesafe-activator-${PLAY_VERSION}.zip -d / && rm typesafe-activator-${PLAY_VERSION}.zip && chmod a+x /activator-dist-${PLAY_VERSION}/

RUN mkdir /app

# create project dir for debug
RUN mkdir /home/app
RUN ln -s /app/conf /home/app/conf
RUN mkdir /home/app/project
RUN ln -s /app/project/build.properties /home/app/project/build.properties
RUN ln -s /app/project/plugins.sbt /home/app/project/plugins.sbt
RUN ln -s /app/project/sbt-ui.sbt /home/app/project/sbt-ui.sbt
RUN ln -s /app/public /home/app/public
RUN ln -s /app/scripts /home/app/scripts
RUN ln -s /app/test /home/app/test
RUN ln -s /app/typings /home/app/typings
RUN ln -s /app/activator.properties /home/app/activator.properties
RUN ln -s /app/build.sbt /home/app/build.sbt
RUN ln -s /app/tsconfig.json /home/app/tsconfig.json
RUN ln -s /app/tslint.json /home/app/tslint.json

EXPOSE 9000
EXPOSE 9999

WORKDIR /home/app

ENV PATH $PATH:/activator-dist
ENV CONF application.conf
RUN mv /activator-dist-${PLAY_VERSION} /activator-dist
RUN echo '#!/bin/bash' > /activator-dist/debug
RUN echo $'/activator-dist/activator clean' >> /activator-dist/debug
RUN echo $'/activator-dist/activator -jvm-debug 9999 -Dconfig.file=/home/app/conf/$CONF ~run' >> /activator-dist/debug
RUN chmod a+x /home/app/

CMD ["sh","/activator-dist/debug"]