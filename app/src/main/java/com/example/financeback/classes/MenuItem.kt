package com.example.financeback.classes

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    var id: String,
    var title: String,
    var contentDescription: String,
    var icon: ImageVector,
    var sensitiveItem: Boolean
)