FROM maven:3.9.3-amazoncorretto-17@sha256:4ab7db7bd5f95e58b0ba1346ff29d6abdd9b73e5fd89c5140edead8b037386ff AS buildtime

WORKDIR /build
COPY . .

# Package goal of maven runs tests that contains testcontainer which causes a docker-in-docker issue
# So skipping test solve the issue
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.7-al2023-headless@sha256:18154896dc03cab39734594c592b73ba506e105e66c81753083cf06235f5c714 AS runtime

# operation needed because amazoncorretto do not contain the shadow-utils package
RUN yum install -y /usr/sbin/adduser
RUN useradd --uid 10000 runner

WORKDIR /app

RUN chown -R runner:runner /app


COPY --from=buildtime /build/target/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.14/applicationinsights-agent-3.4.14.jar /app/applicationinsights-agent.jar

USER 10000

ENTRYPOINT ["java","-jar","/app/app.jar"]
