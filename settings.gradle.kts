rootProject.name = "Trowel"
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
