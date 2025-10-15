package com.ikiugu.oldmutual.presentation.ui.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getGridColumns(configuration: Configuration): GridCells {
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> GridCells.Fixed(2) // Small screens: 2 columns
        screenWidth < 840.dp -> GridCells.Fixed(3) // Medium screens: 3 columns
        else -> GridCells.Fixed(4) // Large screens: 4 columns
    }
}

fun getGridSpacing(configuration: Configuration): Dp {
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> 8.dp // Small screens: tighter spacing
        screenWidth < 840.dp -> 12.dp // Medium screens: moderate spacing
        else -> 16.dp // Large screens: more spacing
    }
}

fun getContentPadding(configuration: Configuration): PaddingValues {
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val horizontalPadding = when {
        screenWidth < 600.dp -> 8.dp
        screenWidth < 840.dp -> 12.dp
        else -> 16.dp
    }
    
    val verticalPadding = if (isLandscape) 8.dp else 16.dp
    
    return PaddingValues(
        horizontal = horizontalPadding,
        vertical = verticalPadding
    )
}

fun getGridColumnsSpanCount(configuration: Configuration): Int {
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> 2 // Small screens: 2 columns
        screenWidth < 840.dp -> 3 // Medium screens: 3 columns
        else -> 4 // Large screens: 4 columns
    }
}

fun getImageSize(configuration: Configuration): Dp {
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    return when {
        screenWidth < 600.dp -> if (isLandscape) 48.dp else 64.dp
        screenWidth < 840.dp -> if (isLandscape) 56.dp else 72.dp
        else -> if (isLandscape) 64.dp else 80.dp
    }
}

fun getCardPadding(configuration: Configuration): Dp {
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> 8.dp
        screenWidth < 840.dp -> 12.dp
        else -> 16.dp
    }
}
