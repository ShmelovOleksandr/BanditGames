plugins {
	id("module-config")
	id("com.adarshr.test-logger") version "4.0.0"
}

dependencies {
	implementation(project(":common"))
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:rabbitmq")
	testImplementation("org.testcontainers:postgresql")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
//	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

subprojects {
	apply {
		plugin("com.adarshr.test-logger")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


// Integration Tests
val integrationTest by tasks.registering(Test::class) {
	description = "Runs integration tests."
	group = "verification"
	useJUnitPlatform()
	include("**/integration/**")
}

// Add all tasks to check lifecycle
tasks.named("check") {
	dependsOn(integrationTest)
}