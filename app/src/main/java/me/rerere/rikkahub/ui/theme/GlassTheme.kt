package me.rerere.rikkahub.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import me.rerere.rikkahub.data.datastore.GlassIntensity
import me.rerere.rikkahub.ui.context.LocalSettings

/**
 * 「液态玻璃（Liquid Glass）」主题的统一参数与助手。
 *
 * 复用显示设置里的 `enableBlurEffect` 开关作为总开关。视觉配方（与预览定稿一致）：
 * - 近乎透明的填充：上亮下暗的白色渐变着色（很淡），背景整片透出来
 * - 背景增强：饱和度提升 + 轻微提亮（"水感"）
 * - 边缘：顶部亮、底部暗的镜面高光细线（液态玻璃的灵魂）
 * - 柔和的浮起外阴影
 * - 强度档：弱/中/强 = 模糊半径 10/14/20dp；轻量模式 = 只做半透明不做模糊
 */
@Immutable
data class GlassConfig(
    val intensity: GlassIntensity,
    val realBlur: Boolean,
) {
    val blurRadius: Dp
        get() = when (intensity) {
            GlassIntensity.LIGHT -> 10.dp
            GlassIntensity.MEDIUM -> 14.dp
            GlassIntensity.STRONG -> 20.dp
        }

    /** 不做真实模糊时（轻量模式/无模糊源）表面的半透明程度。 */
    val liteAlpha: Float
        get() = when (intensity) {
            GlassIntensity.LIGHT -> 0.45f
            GlassIntensity.MEDIUM -> 0.35f
            GlassIntensity.STRONG -> 0.25f
        }

    /** Haze 的模糊+着色配方：饱和度增强 + 上亮下暗的白色渐变着色。 */
    fun hazeStyle(dark: Boolean): HazeBlurStyle = HazeBlurStyle(
        blurRadius = blurRadius,
        colorEffects = listOf(
            HazeColorEffect.ColorFilter(
                colorFilter = ColorFilter.colorMatrix(
                    saturationBrightnessMatrix(saturation = 1.7f, brightness = 1.06f)
                )
            ),
            HazeColorEffect.TintBrush(brush = glassTintBrush(dark)),
        ),
    )
}

/** 饱和度 × 亮度的组合颜色矩阵（标准 luminance 权重）。 */
private fun saturationBrightnessMatrix(saturation: Float, brightness: Float): ColorMatrix {
    val lr = 0.213f
    val lg = 0.715f
    val lb = 0.072f
    val invSat = 1f - saturation
    val r = lr * invSat
    val g = lg * invSat
    val b = lb * invSat
    return ColorMatrix(
        floatArrayOf(
            (r + saturation) * brightness, g * brightness, b * brightness, 0f, 0f,
            r * brightness, (g + saturation) * brightness, b * brightness, 0f, 0f,
            r * brightness, g * brightness, (b + saturation) * brightness, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        )
    )
}

/** 玻璃填充：极淡的白色竖向渐变（上亮下暗）。 */
private fun glassTintBrush(dark: Boolean): Brush = Brush.verticalGradient(
    0f to Color.White.copy(alpha = if (dark) 0.09f else 0.18f),
    0.45f to Color.White.copy(alpha = if (dark) 0.03f else 0.05f),
    1f to Color.White.copy(alpha = if (dark) 0.06f else 0.11f),
)

/** 边缘高光：顶部最亮的竖向渐变细线。 */
private fun glassBorderBrush(dark: Boolean): Brush {
    val top = if (dark) 0.22f else 0.65f
    return Brush.verticalGradient(
        0f to Color.White.copy(alpha = top),
        0.5f to Color.White.copy(alpha = top * 0.5f),
        1f to Color.White.copy(alpha = top * 0.2f),
    )
}

/** 当前页面的真实模糊源（壁纸）。由 App 根与聊天页各自提供。 */
val LocalGlassHazeState = compositionLocalOf<HazeState?> { null }

@Composable
fun rememberGlassConfig(): GlassConfig? {
    val displaySetting = LocalSettings.current.displaySetting
    return if (displaySetting.enableBlurEffect) {
        GlassConfig(displaySetting.glassIntensity, realBlur = !displaySetting.glassLiteMode)
    } else {
        null
    }
}

/** 一个玻璃表面：该用的颜色 + 该加的修饰符（阴影/高光边/模糊/裁剪都在里面）。 */
class GlassSurface(val color: Color, val modifier: Modifier)

@Composable
fun rememberGlassSurface(
    containerColor: Color,
    shape: Shape = RectangleShape,
    config: GlassConfig? = rememberGlassConfig(),
    withBorder: Boolean = true,
): GlassSurface {
    if (config == null) return GlassSurface(containerColor, Modifier)
    val dark = isSystemInDarkTheme()
    val shadowColor = if (dark) Color(0x73000000) else Color(0x263C2814)
    var modifier: Modifier = Modifier.shadow(
        elevation = 10.dp,
        shape = shape,
        clip = false,
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )
    if (withBorder) {
        modifier = modifier.border(1.dp, glassBorderBrush(dark), shape)
    }
    if (config.realBlur) {
        val hazeState = LocalGlassHazeState.current
        if (hazeState != null) {
            val style = config.hazeStyle(dark)
            return GlassSurface(
                color = Color.Transparent,
                modifier = modifier.clip(shape).hazeEffect(hazeState) {
                    blurEffect {
                        blurRadius = config.blurRadius
                        this.style = style
                    }
                },
            )
        }
    }
    return GlassSurface(containerColor.copy(alpha = config.liteAlpha), modifier)
}

/** 全 App 背景图（设置 → 应用背景图）。聊天页仍优先显示助手自己的背景图。 */
@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val displaySetting = LocalSettings.current.displaySetting
    val hazeState = LocalGlassHazeState.current
    val uri = displaySetting.appBackground ?: return
    AsyncImage(
        model = uri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = if (hazeState != null) modifier.hazeSource(hazeState) else modifier,
    )
}
