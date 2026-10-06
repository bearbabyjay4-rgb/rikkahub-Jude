package me.rerere.rikkahub.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import me.rerere.rikkahub.data.datastore.GlassIntensity
import me.rerere.rikkahub.ui.context.LocalSettings

/**
 * 「毛玻璃主题」的统一参数与助手。
 *
 * 复用显示设置里的 `enableBlurEffect` 开关作为总开关；开启后聊天页（顶栏/气泡/输入栏）与
 * 列表、设置等界面统一走这里的换算，保证玻璃质感一致：
 * - 弱 = 模糊 8dp + 玻璃更实；中 = 16dp；强 = 24dp + 更通透（默认）
 * - 轻量模式 = 只做半透明、不做真实背景模糊（省电）
 */
@Immutable
data class GlassConfig(
    val intensity: GlassIntensity,
    val realBlur: Boolean,
) {
    val blurRadius: Dp
        get() = when (intensity) {
            GlassIntensity.LIGHT -> 8.dp
            GlassIntensity.MEDIUM -> 16.dp
            GlassIntensity.STRONG -> 24.dp
        }

    /** 不做真实模糊时（轻量模式/无模糊源）表面的半透明程度。 */
    val alpha: Float
        get() = when (intensity) {
            GlassIntensity.LIGHT -> 0.85f
            GlassIntensity.MEDIUM -> 0.72f
            GlassIntensity.STRONG -> 0.55f
        }

    @Composable
    @ReadOnlyComposable
    fun hazeStyle(containerColor: Color): HazeBlurStyle {
        return when (intensity) {
            GlassIntensity.LIGHT -> HazeMaterials.regular(containerColor)
            GlassIntensity.MEDIUM -> HazeMaterials.thin(containerColor)
            GlassIntensity.STRONG -> HazeMaterials.ultraThin(containerColor)
        }
    }
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

/** 一个玻璃表面：该用的颜色 + 该加的模糊修饰符（裁剪由调用方的 shape/clip 负责）。 */
class GlassSurface(val color: Color, val modifier: Modifier)

@Composable
fun rememberGlassSurface(
    containerColor: Color,
    config: GlassConfig? = rememberGlassConfig(),
): GlassSurface {
    if (config == null) return GlassSurface(containerColor, Modifier)
    if (config.realBlur) {
        val hazeState = LocalGlassHazeState.current
        if (hazeState != null) {
            val style = config.hazeStyle(containerColor)
            return GlassSurface(
                color = Color.Transparent,
                modifier = Modifier.hazeEffect(hazeState) {
                    blurEffect {
                        blurRadius = config.blurRadius
                        this.style = style
                    }
                },
            )
        }
    }
    return GlassSurface(containerColor.copy(alpha = config.alpha), Modifier)
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
