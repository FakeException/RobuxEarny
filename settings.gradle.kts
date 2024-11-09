pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
        maven("https://artifactory.appodeal.com/appodeal")
        maven("https://artifactory.appodeal.com/appodeal-beta-public")
    }
}

rootProject.name = "RobuxEarny"
include(":app")

plugins {
    id("de.fayard.refreshVersions")
}