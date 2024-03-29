plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm")
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("commons-validator:commons-validator:1.7")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("org.springframework:spring-web:6.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.4")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.1")
    implementation("org.json:json:20230227")
    implementation(kotlin("stdlib-jdk8"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.novacore"
                artifactId = "Nova-core"
                version = "1.0.0"
                from(components["java"])
            }
        }
    }
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}