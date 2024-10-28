rootProject.name = "particles"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
    }
}

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
    }
    versionCatalogs {
        create("idofrontLibs").from("com.mineinabyss:catalog:$idofrontVersion")
    }
}


includeBuild("../geary-papermc")
