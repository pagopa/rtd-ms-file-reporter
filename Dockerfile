FROM public.ecr.aws/docker/library/maven:3.9.3-amazoncorretto-17@sha256:4ab7db7bd5f95e58b0ba1346ff29d6abdd9b73e5fd89c5140edead8b037386ff AS buildtime

WORKDIR /build
COPY . .

# Package goal of maven runs tests that contains testcontainer which causes a docker-in-docker issue
# So skipping test solve the issue
RUN mvn clean package -DskipTests

FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre@sha256:97077b491447b095b0fe8d6d6863526dec637b3e6f8f34e50787690b529253f3 AS runtime

WORKDIR /app

COPY --from=buildtime /build/target/*.jar /app/app.jar
RUN chown -R nobody:nobody /app

USER 65534

ENTRYPOINT ["java","-jar","/app/app.jar"]
