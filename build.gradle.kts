// ─── Versions ───────────────────────────────────────────────
val junitVersion         = "5.10.0"
val selenideVersion      = "7.14.0"
val lombokVersion        = "1.18.38"
val javafakerVersion     = "1.0.2"
val ownerVersion         = "1.0.12"
val restAssuredVersion   = "6.0.0"
val log4jVersion         = "2.25.3"
val jacksonVersion       = "2.21.2"
val assertjVersion       = "3.27.7"
val postgresqlVersion    = "42.7.10"
val dbUtilsVersion       = "1.8.1"
val allureVersion        = "2.33.0"
val seleniumVersion      = "4.41.0"
val aspectjVersion       = "1.9.25.1"

plugins {
    id("java")
    id("io.qameta.allure") version "3.2.0"
}

group = "kg.benext"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.codeborne:selenide:$selenideVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("com.github.javafaker:javafaker:$javafakerVersion")
    implementation("org.aeonbits.owner:owner-java8:$ownerVersion")
    implementation("io.rest-assured:rest-assured:$restAssuredVersion")

    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("commons-dbutils:commons-dbutils:$dbUtilsVersion")

    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")

    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    runtimeOnly("org.aspectj:aspectjweaver:$aspectjVersion")
    implementation("org.aspectj:aspectjtools:$aspectjVersion")

    implementation("org.hibernate.orm:hibernate-core:7.3.1.Final")
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.mchange:c3p0:0.12.0")
    implementation("org.awaitility:awaitility:4.3.0")
    implementation("org.mongodb:mongodb-driver-sync:5.6.5")
    implementation("org.seleniumhq.selenium:selenium-devtools-v137:4.35.0")
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    systemProperty("allure.results.directory", "build/allure-results")
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
    }
    systemProperty("selenide.headless", "true")
}

tasks.register<Test>("smokeTest") {
    description = "Runs smoke tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("SMOKE")
    }
}

tasks.register<Test>("regressionTest") {
    description = "Runs regression tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("REGRESSION")
    }
}

tasks.register<Test>("e2eTest") {
    description = "Runs end-to-end tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("E2E")
    }
}

tasks.register<Test>("uiTest") {
    description = "Runs UI tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("UI")
    }
}

tasks.register<Test>("apiTest") {
    description = "Runs API tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("API")
    }
}

tasks.register<Test>("dbTest") {
    description = "Runs DB tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("DB")
    }
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("UNIT")
    }
}