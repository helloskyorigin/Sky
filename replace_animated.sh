#!/bin/bash
sed -i 's/var isVisible by remember { mutableStateOf(false) }/var isVisible by rememberSaveable { mutableStateOf(false) }/' app/src/main/java/com/example/DashboardScreen.kt
sed -i 's/import androidx.compose.runtime.\*/import androidx.compose.runtime.*\nimport androidx.compose.runtime.saveable.rememberSaveable/' app/src/main/java/com/example/DashboardScreen.kt
