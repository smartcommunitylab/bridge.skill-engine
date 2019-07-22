FROM maven:3-jdk-11 AS mvn
WORKDIR /tmp
RUN git clone -b develop https://github.com/fbk/utils tint-develop
RUN git clone -b develop https://github.com/dhfbk/tint tint
WORKDIR /tmp/tint-develop
RUN mvn clean install
WORKDIR /tmp/tint
RUN mvn clean install
COPY ./pom.xml /tmp/skill-engine/pom.xml
COPY ./src /tmp/skill-engine/src
WORKDIR /tmp/skill-engine
RUN mvn clean install -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk11:alpine
ENV FOLDER=/tmp/skill-engine/target
ARG VER=0.1
ENV APP=skill-engine-${VER}.jar
ARG USER=bridge
ARG USER_ID=3002
ARG USER_GROUP=bridge
ARG USER_GROUP_ID=3002
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  /home/${USER}/app
RUN chown ${USER}:${USER_GROUP} /home/${USER}/app
RUN mkdir indexes && chown ${USER}:${USER_GROUP} indexes
# COPY --chown=aac-org:aac-org ./init.sh /tmp/server/target/init.sh
# #COPY ./app.jar /tmp/server/target/app.jar
COPY --from=mvn --chown=bridge:bridge ${FOLDER}/${APP} /home/${USER}/app/bridge.jar

USER bridge
CMD ["java", "-jar", "${APP}"]
