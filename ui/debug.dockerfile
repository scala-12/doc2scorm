FROM ingensi/oracle-jdk

ARG PLAY_VERSION=1.3.10
ENV ACTIVATOR_HOME /activator-dist

RUN \
	yum update -y \
	&& yum install -y unzip
RUN \
	yum install -y samba-client \
	&& yum install -y cifs-utils
RUN \
	curl -O http://downloads.typesafe.com/typesafe-activator/${PLAY_VERSION}/typesafe-activator-${PLAY_VERSION}.zip \
	&& unzip typesafe-activator-${PLAY_VERSION}.zip -d / \
	&& rm typesafe-activator-${PLAY_VERSION}.zip \
	&& chmod a+x /activator-dist-${PLAY_VERSION}/ \
	&& mv /activator-dist-${PLAY_VERSION} ${ACTIVATOR_HOME}

# LibreOffice installation
RUN \
	yum install -y libreoffice \
	&& yum install -y libreoffice-headless
# TrueType fonts installation 
RUN wget -P /tmp http://www.itzgeek.com/msttcore-fonts-2.0-3.noarch.rpm \
	&& yum localinstall -y /tmp/msttcore-fonts-2.0-3.noarch.rpm
ARG SOFFICE=/usr/bin/soffice

RUN mkdir /app

ARG APP_HOME=/home/app
# create project dir for debug
RUN \
	mkdir ${APP_HOME} \
	&& mkdir ${APP_HOME}/project \
	&& ln -s /app/conf ${APP_HOME}/conf \
	&& ln -s /app/project/build.properties ${APP_HOME}/project/build.properties \
	&& ln -s /app/project/plugins.sbt ${APP_HOME}/project/plugins.sbt \
	&& ln -s /app/project/sbt-ui.sbt ${APP_HOME}/project/sbt-ui.sbt \
	&& ln -s /app/public ${APP_HOME}/public \
	&& ln -s /app/scripts ${APP_HOME}/scripts \
	&& ln -s /app/test ${APP_HOME}/test \
	&& ln -s /app/typings ${APP_HOME}/typings \
	&& ln -s /app/activator.properties ${APP_HOME}/activator.properties \
	&& ln -s /app/build.sbt ${APP_HOME}/build.sbt \
	&& ln -s /app/tsconfig.json ${APP_HOME}/tsconfig.json \
	&& ln -s /app/tslint.json ${APP_HOME}/tslint.json \
	&& chmod a+x ${APP_HOME}

WORKDIR /home/app

RUN echo '#!/bin/bash' > ${ACTIVATOR_HOME}/debug \
	&& echo ${ACTIVATOR_HOME}'/bin/activator clean' >> ${ACTIVATOR_HOME}/debug \
	&& echo ${ACTIVATOR_HOME}'/bin/activator -jvm-debug ${DEBUG_PORT:-9999} "-Dconfig.file=/home/app/conf/${APP_CONFIG:-application.debug.conf}" ~run "-DSOFFICE='${SOFFICE}'"' >> ${ACTIVATOR_HOME}/debug

RUN chmod a+x ${ACTIVATOR_HOME}/debug

CMD ${ACTIVATOR_HOME}/debug