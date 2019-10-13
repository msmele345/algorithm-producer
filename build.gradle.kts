import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
}

group = "com.producer"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}


//	compile ("org.apache.kafka:kafka-clients:2.0.0") //double check
dependencies {
    compile("org.springframework:spring-beans")
    compile("org.springframework:spring-context")
    compile("com.github.javafaker:javafaker:0.15")
    compile("org.springframework.integration:spring-integration-kafka:3.1.5.RELEASE")
    compile("org.springframework.kafka:spring-kafka:2.2.7.RELEASE")


    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.integration:spring-integration-file")
    testCompile("org.mockito:mockito-core:2.8.47")
    testCompile("com.nhaarman:mockito-kotlin:1.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.integration:spring-integration-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
