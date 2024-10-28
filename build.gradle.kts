import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.nms)
    alias(idofrontLibs.plugins.mia.autoversion)
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
}

dependencies {
    compileOnly(idofrontLibs.bundles.idofront.core)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(libs.geary.papermc)
    compileOnly(idofrontLibs.idofront.nms)
}

paper {
    main = "com.mineinabyss.particles.ParticlesPlugin"
    name = "Particles"
    val version: String by project
    this.version = version
    authors = listOf("Offz")
    apiVersion = "1.21"

    serverDependencies {
        register("Idofront") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
        register("Geary") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}
