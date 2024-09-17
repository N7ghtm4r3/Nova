plugins {
    id("java")
    id("maven-publish")
    id("org.springframework.boot") version "3.2.3"
    kotlin("jvm")
}

group = "com.tecknobit"
version = "1.0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.clojars.org")
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.4")
    implementation("org.json:json:20231013")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    implementation("com.github.N7ghtm4r3:Mantis:1.0.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.tecknobit.novacore:novacore:1.0.1")
    implementation("com.github.N7ghtm4r3:Equinox:1.0.3")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.nova"
                artifactId = "Nova"
                version = "1.0.1"
                from(components["java"])
            }
        }
    }
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }