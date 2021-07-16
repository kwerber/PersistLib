plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    id("me.champeau.jmh") version "0.6.5"
    id("de.kwerber.ghpub") version "0.3"
}

repositories {
    // Use maven central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.openjdk.jmh", "jmh-core", "1.32")
    testImplementation("org.openjdk.jmh", "jmh-generator-annprocess", "1.32")

    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    testImplementation("org.mariadb.jdbc", "mariadb-java-client", "2.7.3")

    // https://mvnrepository.com/artifact/org.hibernate.javax.persistence/hibernate-jpa-2.1-api
    api("javax.persistence", "javax.persistence-api", "2.2");

    // https://mvnrepository.com/artifact/ch.vorburger.mariaDB4j/mariaDB4j
    implementation("ch.vorburger.mariaDB4j", "mariaDB4j", "2.4.0")

    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc", "mariadb-java-client", "2.7.2")
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

githubPublish {
    committerName.set("kwerber")
    committerEmail.set("werberkevin@gmail.com")
    githubRepoUrl.set(uri("https://github.com/kwerber/maven_repo"))
    githubRepoBranch.set("test")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.kwerber"
            artifactId = "persistlib"
            version = "0.3"

            from(components["java"])
        }
    }
}