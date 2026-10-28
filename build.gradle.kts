plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "top.jaxlabs"
version = "1.5.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.onarandombox.com/content/groups/public/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.1.0")
    compileOnly("me.clip:placeholderapi:2.11.5")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.cronutils:cron-utils:9.2.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", "top.jaxlabs.farmweltplugin.utils.bstats")
        relocate("com.cronutils", "top.jaxlabs.farmweltplugin.libs.cronutils")
        relocate("org.slf4j", "top.jaxlabs.farmweltplugin.libs.slf4j")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("project" to mapOf("version" to project.version))
        }
    }
}
