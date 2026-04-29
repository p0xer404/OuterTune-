/*
 * Copyright (C) 2025 OuterTune Project
 *
 * SPDX-License-Identifier: GPL-3.0
 */

package com.dd3boh.outertune.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated waveform visualizer.
 * Shows bouncing bars when [isPlaying] is true,
 * and a calm sine-wave shape when paused.
 */
@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    color: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    barCount: Int = 36,
    barWidth: Dp = 3.dp,
    barSpacing: Dp = 2.5.dp,
    cornerRadius: Dp = 2.dp,
) {
    val minHeightFraction = 0.07f

    val barHeights = remember(barCount) {
        List(barCount) { i ->
            val pos = i.toFloat() / barCount
            Animatable(0.22f + 0.10f * sin(pos * PI.toFloat() * 4).toFloat())
        }
    }

    barHeights.forEachIndexed { index, animatable ->
        LaunchedEffect(isPlaying, index) {
            if (isPlaying) {
                delay(index * 18L)
                while (true) {
                    val target = Random.nextFloat() * (0.90f - minHeightFraction) + minHeightFraction
                    animatable.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = Random.nextInt(280, 650),
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            } else {
                val pos = index.toFloat() / barCount
                val targetHeight = 0.22f + 0.10f * sin(pos * PI.toFloat() * 4).toFloat()
                animatable.animateTo(
                    targetValue = targetHeight,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Canvas(modifier = modifier) {
        val barWidthPx  = barWidth.toPx()
        val spacingPx   = barSpacing.toPx()
        val cornerRadPx = cornerRadius.toPx()
        val stride      = barWidthPx + spacingPx
        val totalWidth  = barCount * stride - spacingPx
        val startX      = (size.width - totalWidth) / 2f
        val alpha       = if (isPlaying) 0.88f else 0.40f

        barHeights.forEachIndexed { index, animatable ->
            val fraction  = animatable.value.coerceIn(minHeightFraction, 1f)
            val barHeight = size.height * fraction
            val x = startX + index * stride
            val y = (size.height - barHeight) / 2f

            drawRoundRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(x, y),
                size = Size(barWidthPx, barHeight),
                cornerRadius = CornerRadius(cornerRadPx, cornerRadPx)
            )
        }
    }
}