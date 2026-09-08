package com.example.ui.gestures

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

fun Modifier.homeScreenGestures(
    enabled: Boolean = true,
    thresholdDp: Dp = 50.dp,
    onDoubleTap: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onTwoFingerSwipeDown: () -> Unit = {}
): Modifier {
    if (!enabled) {
        return this.pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                if (dragAmount < -20f) {
                    onSwipeUp()
                }
            }
        }
    }

    return this.pointerInput(enabled, thresholdDp) {
        detectHomeScreenGestures(
            thresholdPx = thresholdDp.toPx(),
            onDoubleTap = onDoubleTap,
            onSwipeDown = onSwipeDown,
            onSwipeUp = onSwipeUp,
            onLongPress = onLongPress,
            onTwoFingerSwipeDown = onTwoFingerSwipeDown
        )
    }
}

suspend fun PointerInputScope.detectHomeScreenGestures(
    thresholdPx: Float,
    onDoubleTap: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit,
    onLongPress: () -> Unit,
    onTwoFingerSwipeDown: () -> Unit
) {
    var lastTapTime = 0L
    var lastTapPos = Offset.Zero
    val touchSlop = viewConfiguration.touchSlop

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downTime = System.currentTimeMillis()
        val startPos = down.position

        var totalDeltaY = 0f
        var totalDeltaX = 0f
        var isActionTriggered = false

        // Check for long press with timeout if finger stays still
        val longPressResult = withTimeoutOrNull(450L) {
            while (true) {
                val event = awaitPointerEvent()
                
                // If 2 fingers down: check two finger drag
                if (event.changes.size >= 2) {
                    val p1 = event.changes[0]
                    val p2 = event.changes[1]
                    val dy1 = p1.positionChange().y
                    val dy2 = p2.positionChange().y
                    totalDeltaY += (dy1 + dy2) / 2f
                    
                    if (totalDeltaY > thresholdPx) {
                        isActionTriggered = true
                        p1.consume()
                        p2.consume()
                        onTwoFingerSwipeDown()
                        return@withTimeoutOrNull true
                    }
                } else if (event.changes.size == 1) {
                    val change = event.changes[0]
                    if (!change.pressed) {
                        // Released before long press timeout
                        return@withTimeoutOrNull false
                    }
                    val delta = change.positionChange()
                    totalDeltaY += delta.y
                    totalDeltaX += delta.x

                    // Check if dragged beyond threshold
                    if (abs(totalDeltaY) > thresholdPx && abs(totalDeltaY) > abs(totalDeltaX) * 1.1f) {
                        isActionTriggered = true
                        change.consume()
                        if (totalDeltaY > 0) {
                            onSwipeDown()
                        } else {
                            onSwipeUp()
                        }
                        return@withTimeoutOrNull true
                    }

                    // If moved beyond touch slop, cancel long press
                    if (abs(totalDeltaY) > touchSlop || abs(totalDeltaX) > touchSlop) {
                        // Keep tracking drag outside timeout
                        return@withTimeoutOrNull true
                    }
                }

                if (event.changes.all { !it.pressed }) {
                    return@withTimeoutOrNull false
                }
            }
            false
        }

        // If timed out without moving beyond slop and not released -> LONG PRESS!
        if (longPressResult == null && !isActionTriggered) {
            isActionTriggered = true
            down.consume()
            onLongPress()
            // Wait for release
            do {
                val event = awaitPointerEvent()
            } while (event.changes.any { it.pressed })
            return@awaitEachGesture
        }

        // If action already triggered during timeout (e.g. quick swipe), wait for release
        if (isActionTriggered) {
            do {
                val event = awaitPointerEvent()
            } while (event.changes.any { it.pressed })
            return@awaitEachGesture
        }

        // If pointer is still down after timeout (e.g. moved slightly), continue monitoring drag until release
        while (true) {
            val event = awaitPointerEvent()
            if (event.changes.all { !it.pressed }) {
                // Pointer released
                val change = event.changes.firstOrNull()
                val isTap = abs(totalDeltaY) < touchSlop && abs(totalDeltaX) < touchSlop
                if (isTap) {
                    val now = System.currentTimeMillis()
                    val dist = (startPos - lastTapPos).getDistance()
                    if (now - lastTapTime < 350L && dist < 120f) {
                        // Double Tap!
                        lastTapTime = 0L
                        change?.consume()
                        onDoubleTap()
                    } else {
                        lastTapTime = now
                        lastTapPos = startPos
                    }
                }
                break
            }

            // Still tracking drag
            if (event.changes.size >= 2) {
                val p1 = event.changes[0]
                val p2 = event.changes[1]
                val dy1 = p1.positionChange().y
                val dy2 = p2.positionChange().y
                totalDeltaY += (dy1 + dy2) / 2f
                if (totalDeltaY > thresholdPx && !isActionTriggered) {
                    isActionTriggered = true
                    p1.consume()
                    p2.consume()
                    onTwoFingerSwipeDown()
                    break
                }
            } else if (event.changes.size == 1) {
                val change = event.changes[0]
                val delta = change.positionChange()
                totalDeltaY += delta.y
                totalDeltaX += delta.x

                if (abs(totalDeltaY) > thresholdPx && abs(totalDeltaY) > abs(totalDeltaX) * 1.1f && !isActionTriggered) {
                    isActionTriggered = true
                    change.consume()
                    if (totalDeltaY > 0) {
                        onSwipeDown()
                    } else {
                        onSwipeUp()
                    }
                    break
                }
            }
        }
    }
}
