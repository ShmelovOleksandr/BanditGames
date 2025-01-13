plugins {
    id("module-config")
    id("com.adarshr.test-logger") version "4.0.0"

}


extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudAzureVersion"] = "5.18.0"

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-amqp") // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
    implementation("org.springframework.boot:spring-boot-starter-jdbc") // JDBC
    implementation("org.springframework.boot:spring-boot-starter-web") // Web MVC
    implementation("com.azure.spring:spring-cloud-azure-starter")
    implementation("com.azure.spring:spring-cloud-azure-starter-jdbc-postgresql")
    implementation("com.azure.spring:spring-cloud-azure-starter-storage")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // Reactive Web (WebFlux)
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

    // OAuth2 Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // CSV Utility
    implementation("com.opencsv:opencsv:4.6")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") // Includes JUnit 5
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter") // Testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.github.dasniko:testcontainers-keycloak:3.5.1")
}

subprojects {
    apply {
        plugin("com.adarshr.test-logger")
    }
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:${property("springCloudAzureVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


// Unit Tests
val unitTest by tasks.registering(Test::class) {
    description = "Runs unit tests."
    group = "verification"
    useJUnitPlatform()
    include("**/unit/**")
}

// Integration Tests
val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    include("**/integration/**")
}

// Add unitTest and integrationTest to the check lifecycle
tasks.named("check") {
    dependsOn(unitTest, integrationTest)
}