pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://developer.huawei.com/repo/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://developer.huawei.com/repo/") }
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "IBAN Detektor"
include(":app")
