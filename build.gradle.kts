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
}

tasks.test {
    useJUnitPlatform()
}