package me.rerere.rikkahub.ui.components.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.uuid.Uuid

/**
 * 「分段发送」的逐条弹出状态汇总（纯 UI 层，不动数据模型）。
 * - targetNodeId：当前正在逐条弹出的消息节点
 * - revealedCounts：节点已弹出的段数（滚动离开再回来可从中断处续播）
 */
@Stable
class AssistantSplitRevealController {
    var targetNodeId: Uuid? by mutableStateOf(null)
        private set

    var isRevealing: Boolean by mutableStateOf(false)
        private set

    private val revealedCounts = mutableStateMapOf<Uuid, Int>()

    /** 开始逐条弹出（第一段立即显示）。 */
    fun start(nodeId: Uuid) {
        revealedCounts[nodeId] = 1
        targetNodeId = nodeId
        isRevealing = true
    }

    /** 该节点已弹出的段数；不是当前目标时返回 null（= 全部直接显示）。 */
    fun revealedCount(nodeId: Uuid): Int? =
        if (targetNodeId == nodeId) revealedCounts[nodeId] ?: 1 else null

    fun finish() {
        isRevealing = false
        targetNodeId = null
    }

    /** 按随机间隔逐段弹出直至全部显示；协程被取消（离开组合）后可按当前进度续播。 */
    suspend fun revealSegments(nodeId: Uuid, segmentCount: Int, minDelayMs: Int, maxDelayMs: Int) {
        if (segmentCount <= 1) {
            finish()
            return
        }
        var count = (revealedCounts[nodeId] ?: 1).coerceAtLeast(1)
        while (count < segmentCount) {
            val lo = minDelayMs.coerceAtLeast(50)
            val hi = maxDelayMs.coerceAtLeast(lo)
            delay(Random.nextLong(lo.toLong(), hi + 1L))
            count += 1
            revealedCounts[nodeId] = count
        }
        finish()
    }
}

/** 逐条弹出参数（只在「当前目标消息 + 最后一个文本块」时向下传）。 */
data class SplitRevealParams(
    val controller: AssistantSplitRevealController,
    val nodeId: Uuid,
    val minDelayMs: Int,
    val maxDelayMs: Int,
)

/** 段首次出现时播放一次滑入+淡入；组合重建时已显示的段不重播。 */
@Composable
internal fun RevealSegmentEnter(
    animate: Boolean,
    content: @Composable () -> Unit,
) {
    if (!animate) {
        content()
    } else {
        val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInVertically { it / 4 } + fadeIn(),
        ) {
            content()
        }
    }
}
