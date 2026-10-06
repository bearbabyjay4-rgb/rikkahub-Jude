package me.rerere.rikkahub.ui.components.message

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import me.rerere.rikkahub.ui.theme.rememberGlassConfig
import me.rerere.rikkahub.ui.theme.rememberGlassSurface

/**
 * 「分段发送」逐条弹出时的等待指示：三个依次明暗跳动的小圆点，装在助手气泡里。
 * 只在「生成完成、等待下一段弹出」的间隙显示；生成过程中的兔子 loading 不受影响。
 */
@Composable
fun TypingIndicatorBubble(modifier: Modifier = Modifier) {
    val glassConfig = rememberGlassConfig()
    val shape = RoundedCornerShape(if (glassConfig != null) 14.dp else 8.dp)
    val glassSurface = rememberGlassSurface(
        containerColor = assistantMessageBubbleColor(),
        config = glassConfig,
    )
    Surface(
        modifier = modifier.then(glassSurface.modifier),
        shape = shape,
        color = glassSurface.color,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val transition = rememberInfiniteTransition(label = "typingDots")
            repeat(3) { index ->
                val alpha by transition.animateFloat(
                    initialValue = 0.25f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(index * 160),
                    ),
                    label = "dot$index",
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)),
                )
            }
        }
    }
}
