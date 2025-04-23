FROM public.ecr.aws/docker/library/maven:3.9.6-amazoncorretto-21@sha256:16dbd3a488a582cff1e42489f67b2b10b466e8a8eb1bdc4a1223d4e949812593 AS buildtime

WORKDIR /build
COPY . .

# Package goal of maven runs tests that contains testcontainer which causes a docker-in-docker issue
# So skipping test solve the issue
RUN mvn clean package -DskipTests

FROM public.ecr.aws/docker/library/eclipse-temurin:21-jre@sha256:242219ed78dc9c09cef8a34808d18d93977dfd3a4cda3c3f18aba44fde444ea7 AS runtime

WORKDIR /app

COPY --from=buildtime /build/target/*.jar /app/app.jar
RUN chown -R nobody:nogroup /app

USER 65534

ENTRYPOINT ["java","-jar","/app/app.jar"]
