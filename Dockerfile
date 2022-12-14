FROM maven:3-jdk-11-slim as buildtime

WORKDIR /build
COPY . .

RUN mvn clean package

FROM amazoncorretto:11 as runtime

WORKDIR /app

COPY --from=buildtime /build/target/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.3/applicationinsights-agent-3.4.3.jar /app/applicationinsights-agent.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
