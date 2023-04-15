repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

val projectMain = project(":${rootProject.name}-main")

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation("io.github.muqhc:skyguifx:0.1.1")
}

val pluginName = rootProject.name.split('-').joinToString("") { it.capitalize() }
val packageName = rootProject.name.replace("-", "")
extra.set("pluginName", pluginName)
extra.set("packageName", packageName)

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    create<Jar>("debugJar") {
        archiveBaseName.set(pluginName)

        from(project.sourceSets["main"].output)
    }
}



