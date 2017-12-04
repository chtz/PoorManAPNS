FROM java:8-jre-alpine
VOLUME /tmp
RUN apk add --update ruby
RUN apk --update add --virtual build_deps build-base ruby-dev libc-dev linux-headers zlib-dev libxml2-dev libxslt-dev
RUN gem install --no-ri --no-rdoc nokogiri -- --use-system-libraries
RUN gem install --no-ri --no-rdoc nomad-cli -- --use-system-libraries
ADD pmapns-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN sh -c 'mkdir /storage'
ENV JAVA_OPTS="-DstoragePath=/storage"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
