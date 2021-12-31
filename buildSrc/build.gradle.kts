plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("ru.curs:celesta-core:7.4.14")
    implementation("ru.curs:celesta-system-services:7.4.14")
    implementation("ru.curs:celesta-sql:7.4.14")
    implementation("ru.curs:celesta-maven-plugin:7.4.15-SNAPSHOT")
}
