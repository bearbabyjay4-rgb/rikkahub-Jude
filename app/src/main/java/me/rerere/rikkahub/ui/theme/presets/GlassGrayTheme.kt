package me.rerere.rikkahub.ui.theme.presets

import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.theme.PresetTheme

/** 「毛玻璃主题」配套的低饱和度灰调预设：开关/选中态/发送键等都是中性灰，玻璃下不抢戏。 */
val GlassGrayThemePreset by lazy {
    PresetTheme(
        id = "glass_gray",
        name = {
            Text(stringResource(id = R.string.theme_name_glass_gray))
        },
        standardLight = lightScheme,
        standardDark = darkScheme,
    )
}

//region Glass Gray Theme Colors (light)
private val primaryLight = Color(0xFF6F6F73)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFE4E4E8)
private val onPrimaryContainerLight = Color(0xFF2A2A2E)
private val secondaryLight = Color(0xFF77767A)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFE7E6EA)
private val onSecondaryContainerLight = Color(0xFF2D2C30)
private val tertiaryLight = Color(0xFF737377)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFE5E4E8)
private val onTertiaryContainerLight = Color(0xFF2B2A2E)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF93000A)
private val backgroundLight = Color(0xFFF7F6F8)
private val onBackgroundLight = Color(0xFF2B2A2D)
private val surfaceLight = Color(0xFFF7F6F8)
private val onSurfaceLight = Color(0xFF2B2A2D)
private val surfaceVariantLight = Color(0xFFE3E3E7)
private val onSurfaceVariantLight = Color(0xFF5A595D)
private val outlineLight = Color(0xFF8A898D)
private val outlineVariantLight = Color(0xFFCCCCD0)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF303034)
private val inverseOnSurfaceLight = Color(0xFFF3F2F5)
private val inversePrimaryLight = Color(0xFFCFCFD4)
private val surfaceDimLight = Color(0xFFE2E1E5)
private val surfaceBrightLight = Color(0xFFFDFCFE)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF9F8FB)
private val surfaceContainerLight = Color(0xFFF4F3F6)
private val surfaceContainerHighLight = Color(0xFFEEEEF1)
private val surfaceContainerHighestLight = Color(0xFFE9E8EB)

//region Glass Gray Theme Colors (dark)
private val primaryDark = Color(0xFFC9C8CD)
private val onPrimaryDark = Color(0xFF313035)
private val primaryContainerDark = Color(0xFF47464B)
private val onPrimaryContainerDark = Color(0xFFE5E4E8)
private val secondaryDark = Color(0xFFC7C6CA)
private val onSecondaryDark = Color(0xFF302F33)
private val secondaryContainerDark = Color(0xFF464549)
private val onSecondaryContainerDark = Color(0xFFE3E2E6)
private val tertiaryDark = Color(0xFFC8C7CB)
private val onTertiaryDark = Color(0xFF303034)
private val tertiaryContainerDark = Color(0xFF454449)
private val onTertiaryContainerDark = Color(0xFFE4E3E7)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF131316)
private val onBackgroundDark = Color(0xFFE5E4E8)
private val surfaceDark = Color(0xFF131316)
private val onSurfaceDark = Color(0xFFE5E4E8)
private val surfaceVariantDark = Color(0xFF464549)
private val onSurfaceVariantDark = Color(0xFFC9C8CC)
private val outlineDark = Color(0xFF908F94)
private val outlineVariantDark = Color(0xFF464549)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE5E4E8)
private val inverseOnSurfaceDark = Color(0xFF303034)
private val inversePrimaryDark = Color(0xFF6F6F73)
private val surfaceDimDark = Color(0xFF131316)
private val surfaceBrightDark = Color(0xFF39393D)
private val surfaceContainerLowestDark = Color(0xFF0E0E11)
private val surfaceContainerLowDark = Color(0xFF1C1B1F)
private val surfaceContainerDark = Color(0xFF201F23)
private val surfaceContainerHighDark = Color(0xFF2A292D)
private val surfaceContainerHighestDark = Color(0xFF363539)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
