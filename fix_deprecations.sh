#!/bin/bash
# Fix Divider -> HorizontalDivider
find app/src/main/java/com/example -type f -name "*.kt" -exec sed -i 's/\bDivider(/HorizontalDivider(/g' {} +
find app/src/main/java/com/example -type f -name "*.kt" -exec sed -i 's/import androidx.compose.material3.Divider/import androidx.compose.material3.HorizontalDivider/g' {} +

# Fix Icons.Filled.ArrowBack -> Icons.AutoMirrored.Filled.ArrowBack
find app/src/main/java/com/example -type f -name "*.kt" -exec sed -i 's/Icons\.Filled\.ArrowBack/Icons.AutoMirrored.Filled.ArrowBack/g' {} +

# Fix Icons.Rounded.ArrowForward -> Icons.AutoMirrored.Rounded.ArrowForward
find app/src/main/java/com/example -type f -name "*.kt" -exec sed -i 's/Icons\.Rounded\.ArrowForward/Icons.AutoMirrored.Rounded.ArrowForward/g' {} +

