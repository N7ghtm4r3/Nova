plugins {
    id("java")
    id("maven-publish")
    id("org.springframework.boot") version "3.2.3"
}

apply(plugin = "io.spring.dependency-management")

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
    //TO-DO: TO REMOVE FOR TESTING PURPOSES ONLY
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.clojars.org")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.2")
    implementation("org.json:json:20230227")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    implementation("com.github.N7ghtm4r3:Mantis:1.0.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    //TO-DO: TO REMOVE FOR TESTING PURPOSES ONLY
    implementation("com.tecknobit.novacore:Nova-core:1.0.0")
}

tasks {
    /*compileKotlin {
        kotlinOptions.jvmTarget = "18"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "18"
    }*/
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.nova"
                artifactId = "Nova"
                version = "1.0.0"
                from(components["java"])
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

/*kotlin {
    jvmToolchain(18)
}*/

configurations.all {
    exclude("commons-logging", "commons-logging")
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }