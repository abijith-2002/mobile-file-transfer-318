package com.app.quicktransfer.ui.theme

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.app.quicktransfer.R

private val FallbackLight = lightColorScheme()
private val FallbackDark = darkColorScheme()

/**
 * Local Reddit Sans font family mapping to our bundled font resources.
 * - Normal -> reddit_sans_regular
 * - Medium -> reddit_sans_medium
 * - SemiBold -> reddit_sans_semibold
 * - Bold -> reddit_sans_bold
 */
private val redditSansFamily = FontFamily(
    Font(resId = R.font.reddit_sans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.reddit_sans_medium, weight = FontWeight.Medium),
    Font(resId = R.font.reddit_sans_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.reddit_sans_bold, weight = FontWeight.Bold),
)

// PUBLIC_INTERFACE
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /**
     * AppTheme applies Material 3 theming with:
     * - Dynamic color on Android 12+ if enabled, else light/dark fallbacks
     * - Global Typography that defaults to local Reddit Sans font family
     *
     * Parameters:
     * - useDarkTheme: Whether dark theme should be used (defaults to system)
     * - dynamicColor: Whether dynamic color should be used on Android 12+ (defaults true)
     * - content: Composable content to render inside the theme
     *
     * Returns:
     * - None. Wraps the provided content with MaterialTheme.
     */
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> if (useDarkTheme) FallbackDark else FallbackLight
    }

    // Set the Reddit Sans as the default font family globally for all Material3 text styles.
    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = redditSansFamily),
        displayMedium = base.displayMedium.copy(fontFamily = redditSansFamily),
        displaySmall = base.displaySmall.copy(fontFamily = redditSansFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = redditSansFamily),
        headlineMedium = base.headlineMedium.copy(fontFamily = redditSansFamily),
        headlineSmall = base.headlineSmall.copy(fontFamily = redditSansFamily),
        titleLarge = base.titleLarge.copy(fontFamily = redditSansFamily),
        titleMedium = base.titleMedium.copy(fontFamily = redditSansFamily),
        titleSmall = base.titleSmall.copy(fontFamily = redditSansFamily),
        bodyLarge = base.bodyLarge.copy(fontFamily = redditSansFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = redditSansFamily),
        bodySmall = base.bodySmall.copy(fontFamily = redditSansFamily),
        labelLarge = base.labelLarge.copy(fontFamily = redditSansFamily),
        labelMedium = base.labelMedium.copy(fontFamily = redditSansFamily),
        labelSmall = base.labelSmall.copy(fontFamily = redditSansFamily)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
