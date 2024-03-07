plugins {
    id("java")
    id("maven-publish")
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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