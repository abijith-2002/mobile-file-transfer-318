androidApplication {
    namespace = "com.app.quicktransfer"
    compileSdk = 36
    targetSdk = 36

    // Enable Jetpack Compose compiler plugin via Declarative Gradle DSL
    compose {
        enabled = true
    }

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
        // Jetpack Compose BOM and core (explicit versions as required)
        implementation("androidx.activity:activity-ktx:1.9.3")
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("androidx.compose.ui:ui:1.7.4")
        implementation("androidx.compose.ui:ui-tooling-preview:1.7.4")
        implementation("androidx.compose.material3:material3:1.3.0")
        implementation("androidx.compose.material3:material3-window-size-class:1.3.0")
        implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
        implementation("androidx.compose.material:material-icons-extended:1.7.4")
        implementation("androidx.compose.foundation:foundation:1.7.4")
        implementation("androidx.compose.foundation:foundation-layout:1.7.4")
        implementation("androidx.compose.animation:animation:1.7.4")
        implementation("androidx.navigation:navigation-compose:2.8.3")
        implementation("androidx.compose.ui:ui-text-google-fonts:1.7.4")
        implementation("androidx.compose.ui:ui-text:1.7.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.annotation:annotation:1.9.1")
        // Prefer JetBrains annotations to avoid conflicts with legacy com.intellij annotations
        implementation("org.jetbrains:annotations:24.1.0")
        // Tooling (only used in debug but included explicitly to ensure previews compile)
        implementation("androidx.compose.ui:ui-tooling:1.7.4")
        implementation("androidx.compose.ui:ui-test-manifest:1.7.4")

        // Room persistence library (runtime only; no annotation processor to avoid com.intellij:annotations conflicts)
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")

    }

    // Declarative testing block to add unit test dependencies
    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}
