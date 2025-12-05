package org.example.app.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

private val FallbackLight = lightColorScheme()
private val FallbackDark = darkColorScheme()

/**
 * Try to build a FontFamily for "Reddit Sans" using the Play Services Google Fonts provider.
 *
 * This implementation avoids referencing any R.array resource directly (which can fail to
 * resolve during compilation in some setups). Instead, it resolves the certificate array ID
 * at runtime via getIdentifier. If the array cannot be found, returns null so callers can
 * gracefully fallback to bundled/system fonts.
 */
private fun tryBuildGoogleFontFamily(context: Context): FontFamily? {
    // Resolve Google Play Services certificate array resource dynamically to avoid direct R.array reference
    val certsResId = context.resources.getIdentifier(
        "com_google_android_gms_fonts_certs",
        "array",
        context.packageName
    )
    if (certsResId == 0) {
        // Certificates resource not available – cannot safely use the provider
        return null
    }

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = certsResId
    )

    val familyName = GoogleFont("Reddit Sans")
    return FontFamily(
        Font(googleFont = familyName, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = familyName, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = familyName, fontProvider = provider, weight = FontWeight.SemiBold)
    )
}

private fun redditSansTypography(context: Context): Typography {
    // Prefer Reddit Sans via Google Fonts; if unavailable, fallback to a safe system sans-serif
    val redditSansOrFallback = tryBuildGoogleFontFamily(context) ?: FontFamily.SansSerif

    return Typography(
        displayLarge = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.SemiBold, fontSize = 57.sp, lineHeight = 64.sp),
        displayMedium = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.SemiBold, fontSize = 45.sp, lineHeight = 52.sp),
        displaySmall = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 44.sp),
        headlineLarge = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
        headlineMedium = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 36.sp),
        headlineSmall = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 32.sp),
        titleLarge = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
        titleSmall = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
        labelLarge = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        labelMedium = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
        labelSmall = TextStyle(fontFamily = redditSansOrFallback, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
    )
}

// PUBLIC_INTERFACE
/**
 * AppTheme configures Material 3 theming for the app.
 *
 * - Uses dynamic color on Android 12+ (when enabled).
 * - Prefers the "Reddit Sans" Google Font via the Play Services provider with a safe runtime
 *   certificate lookup. If unavailable, falls back to the system sans-serif family.
 * - Avoids direct references to array resources (e.g., R.array.*) and ResourcesCompat APIs.
 *
 * @param useDarkTheme Whether to use the dark color scheme when dynamic color is disabled.
 * @param dynamicColor Whether to enable Material You dynamic color on Android 12+.
 * @param content The composable tree to render inside the theme.
 */
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> if (useDarkTheme) FallbackDark else FallbackLight
    }

    val typography = redditSansTypography(context)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
