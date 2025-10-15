package com.ikiugu.oldmutual.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ikiugu.oldmutual.R
import com.ikiugu.oldmutual.ui.theme.*

@Composable
fun PokemonLoader(
    modifier: Modifier = Modifier,
    message: String = "Loading Pokémon..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pokemonLoader")
    
    // Animated scale for the main circle
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Animated rotation for the outer ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Animated color gradient
    val colorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorProgress"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TypeElectric.copy(alpha = 0.1f),
                            TypeWater.copy(alpha = 0.1f),
                            TypeFire.copy(alpha = 0.1f)
                        )
                    )
                )
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main loading animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Outer rotating ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    TypeElectric,
                                    TypeWater,
                                    TypeFire,
                                    TypeGrass,
                                    TypeElectric
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // Inner pulsing circle
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale * 0.8f)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                )
                
                // Center dot
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            TypeElectric,
                            CircleShape
                        )
                )
            }
            
            // Loading text
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f)
            )
            
            // Animated dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale)
                            .background(
                                TypeElectric,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
