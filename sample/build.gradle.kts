import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.foundation)
            implementation(libs.compose.components.resources)
            implementation(compose.desktop.currentOs)

            // Advanced-Menubar
            implementation(projects.advancedMenubar)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Advanced-Menubar Sample"
            packageVersion = "1.0.0"
            macOS {
                dockName = "Advanced-Menubar Sample"
            }
        }

        buildTypes.release.proguard {
            isEnabled = true
            obfuscate = true
            optimize = true
            configurationFiles.from(project.file("src/desktopMain/compose-desktop.pro"))
        }
    }
}