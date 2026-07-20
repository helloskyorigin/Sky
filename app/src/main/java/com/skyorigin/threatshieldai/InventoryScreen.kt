package com.skyorigin.threatshieldai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyorigin.threatshieldai.ui.theme.LocalIsDark
import com.skyorigin.threatshieldai.ui.theme.PremiumColors
import com.skyorigin.threatshieldai.ui.theme.PremiumTypography
import com.skyorigin.threatshieldai.ui.theme.premiumShadow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ScamLensViewModel,
    onBack: () -> Unit = {}
) {
    val isDark = LocalIsDark.current
    val primaryBlue = PremiumColors.PrimaryAccent
    val textDark = MaterialTheme.colorScheme.onBackground
    val textGray = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF252932) else Color(0xFFDCE3EE)
    val successGreen = Color(0xFF22C55E)
    val warnOrange = Color(0xFFF59E0B)
    val dangerRed = Color(0xFFEF4444)

    val itemsState by viewModel.allInventoryItems.collectAsState(initial = emptyList())

    // Search and filters
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var selectedSortOption by rememberSaveable { mutableStateOf("Last Updated") }

    // Dialog flags
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<InventoryItemEntity?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    // Smooth fade in
    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
    }

    // Categories list
    val categories = listOf("All", "Electronics", "Office", "Food", "Clothes", "Hardware", "Others")

    // Sort, search, filter operations
    val filteredItems = remember(itemsState, searchQuery, selectedCategory, selectedSortOption) {
        itemsState.filter {
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || 
                                it.sku.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
            matchesSearch && matchesCategory
        }.sortedWith { a, b ->
            when (selectedSortOption) {
                "Name" -> a.name.compareTo(b.name, ignoreCase = true)
                "Stock (High to Low)" -> b.quantity.compareTo(a.quantity)
                "Stock (Low to High)" -> a.quantity.compareTo(b.quantity)
                else -> b.lastUpdated.compareTo(a.lastUpdated) // Last Updated
            }
        }
    }

    // Totals calculations
    val totalItems = itemsState.size
    val totalStockQuantity = itemsState.sumOf { it.quantity }
    val totalInventoryValue = itemsState.sumOf { it.quantity * it.price }
    val lowStockCount = itemsState.count { it.quantity < 5 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventory Manager",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textDark,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = textDark
                        )
                    }
                },
                actions = {
                    if (totalItems > 0) {
                        IconButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.testTag("clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = "Clear All",
                                tint = dangerRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = primaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("add_item_fab")
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add New Item",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 1. Dashboard Header Statistics Card
                item {
                    val brush = Brush.horizontalGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        } else {
                            listOf(Color(0xFFEEF2F6), Color(0xFFE2E8F0))
                        }
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .premiumShadow(
                                isDark = isDark,
                                borderRadius = 20.dp
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush)
                                .padding(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "STOCK SUMMARY",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryBlue,
                                        letterSpacing = 1.sp
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "$totalItems",
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textDark
                                            )
                                        )
                                        Text(
                                            text = "Unique Items",
                                            style = TextStyle(fontSize = 12.sp, color = textGray)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1.2f)) {
                                        Text(
                                            text = "$totalStockQuantity",
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textDark
                                            )
                                        )
                                        Text(
                                            text = "Total Quantity",
                                            style = TextStyle(fontSize = 12.sp, color = textGray)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text(
                                            text = "$${String.format("%.2f", totalInventoryValue)}",
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textDark
                                            )
                                        )
                                        Text(
                                            text = "Total Value",
                                            style = TextStyle(fontSize = 12.sp, color = textGray)
                                        )
                                    }
                                }

                                if (lowStockCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = warnOrange.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Warning,
                                            contentDescription = "Warning",
                                            tint = warnOrange,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "$lowStockCount items are running low on stock (< 5 units)!",
                                            style = TextStyle(
                                                fontSize = 11.sp,
                                                color = warnOrange,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Search & Sort Bar
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name or SKU...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Search",
                                    tint = textGray
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Clear",
                                            tint = textGray
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_bar"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = cardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by:",
                                style = TextStyle(fontSize = 12.sp, color = textGray, fontWeight = FontWeight.Medium)
                            )
                            listOf("Last Updated", "Name", "Stock (High to Low)", "Stock (Low to High)").forEach { option ->
                                val selected = selectedSortOption == option
                                AssistChip(
                                    onClick = { selectedSortOption = option },
                                    label = { Text(option) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selected) primaryBlue.copy(alpha = 0.15f) else Color.Transparent,
                                        labelColor = if (selected) primaryBlue else textGray
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(
                                        enabled = true,
                                        borderColor = if (selected) primaryBlue else cardBorder
                                    )
                                )
                            }
                        }
                    }
                }

                // 3. Categories Horizontal Selector List
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) primaryBlue else if (isDark) Color(0xFF1E293B) else Color(
                                            0xFFF1F5F9
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) primaryBlue else cardBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else textDark
                                    )
                                )
                            }
                        }
                    }
                }

                // 4. Inventory List Items
                if (filteredItems.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Category,
                                contentDescription = "No Items",
                                tint = textGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedCategory != "All") "No items match your filters." else "Your inventory is currently empty.",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textDark,
                                    textAlign = TextAlign.Center
                                )
                            )
                            if (searchQuery.isEmpty() && selectedCategory == "All") {
                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                                ) {
                                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Your First Item")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        InventoryItemRow(
                            item = item,
                            isDark = isDark,
                            primaryBlue = primaryBlue,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textDark = textDark,
                            textGray = textGray,
                            successGreen = successGreen,
                            warnOrange = warnOrange,
                            dangerRed = dangerRed,
                            onEdit = {
                                itemToEdit = item
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.deleteInventoryItem(item)
                            }
                        )
                    }
                }
            }

            // --- Add Dialog ---
            if (showAddDialog) {
                InventoryEditDialog(
                    title = "Add Inventory Item",
                    isDark = isDark,
                    cardBg = cardBg,
                    primaryBlue = primaryBlue,
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, sku, quantity, price, category, description ->
                        viewModel.insertInventoryItem(
                            InventoryItemEntity(
                                name = name,
                                sku = sku,
                                quantity = quantity,
                                price = price,
                                category = category,
                                description = description
                            )
                        )
                        showAddDialog = false
                    }
                )
            }

            // --- Edit Dialog ---
            if (showEditDialog && itemToEdit != null) {
                val item = itemToEdit!!
                InventoryEditDialog(
                    title = "Edit Inventory Item",
                    isDark = isDark,
                    cardBg = cardBg,
                    primaryBlue = primaryBlue,
                    initialItem = item,
                    onDismiss = {
                        showEditDialog = false
                        itemToEdit = null
                    },
                    onConfirm = { name, sku, quantity, price, category, description ->
                        viewModel.updateInventoryItem(
                            item.copy(
                                name = name,
                                sku = sku,
                                quantity = quantity,
                                price = price,
                                category = category,
                                description = description,
                                lastUpdated = System.currentTimeMillis()
                            )
                        )
                        showEditDialog = false
                        itemToEdit = null
                    }
                )
            }

            // --- Clear Confirm Dialog ---
            if (showClearConfirm) {
                AlertDialog(
                    onDismissRequest = { showClearConfirm = false },
                    title = { Text("Delete All Items?") },
                    text = { Text("Are you absolutely sure you want to delete all items from your inventory? This cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.clearAllInventory()
                                showClearConfirm = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = dangerRed)
                        ) {
                            Text("Delete All", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearConfirm = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemEntity,
    isDark: Boolean,
    primaryBlue: Color,
    cardBg: Color,
    cardBorder: Color,
    textDark: Color,
    textGray: Color,
    successGreen: Color,
    warnOrange: Color,
    dangerRed: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .premiumShadow(
                isDark = isDark,
                borderRadius = 16.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Category Badge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(primaryBlue.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint = primaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = dangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Name & SKU
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "SKU: ${item.sku.ifEmpty { "N/A" }}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textGray,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
            }

            if (item.description.isNotEmpty()) {
                Text(
                    text = item.description,
                    style = TextStyle(fontSize = 13.sp, color = textGray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Divider(color = cardBorder, modifier = Modifier.padding(vertical = 4.dp))

            // Footer Row: Price & Quantity Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Unit Price",
                        style = TextStyle(fontSize = 11.sp, color = textGray)
                    )
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textDark
                        )
                    )
                }

                // Low Stock / Out of Stock Indicators
                val statusText = when {
                    item.quantity == 0 -> "OUT OF STOCK"
                    item.quantity < 5 -> "LOW STOCK"
                    else -> "IN STOCK"
                }
                val statusColor = when {
                    item.quantity == 0 -> dangerRed
                    item.quantity < 5 -> warnOrange
                    else -> successGreen
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }

                    Text(
                        text = "${item.quantity} units",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryEditDialog(
    title: String,
    isDark: Boolean,
    cardBg: Color,
    primaryBlue: Color,
    initialItem: InventoryItemEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, sku: String, quantity: Int, price: Double, category: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var sku by remember { mutableStateOf(initialItem?.sku ?: "") }
    var quantityStr by remember { mutableStateOf(initialItem?.quantity?.toString() ?: "") }
    var priceStr by remember { mutableStateOf(initialItem?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Electronics") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    val categoriesList = listOf("Electronics", "Office", "Food", "Clothes", "Hardware", "Others")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.trim().isEmpty()
                    },
                    label = { Text("Item Name *") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = {
                            quantityStr = it
                            val parsed = it.toIntOrNull()
                            quantityError = parsed == null || parsed < 0
                        },
                        label = { Text("Qty *") },
                        isError = quantityError,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = {
                            priceStr = it
                            val parsed = it.toDoubleOrNull()
                            priceError = parsed == null || parsed < 0
                        },
                        label = { Text("Price ($) *") },
                        isError = priceError,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Category dropdown-ish Selector
                Text(
                    text = "Category",
                    style = TextStyle(fontSize = 12.sp, color = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoriesList.forEach { cat ->
                        val selected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) primaryBlue else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                .border(1.dp, if (selected) primaryBlue else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQty = quantityStr.toIntOrNull()
                    val parsedPrice = priceStr.toDoubleOrNull()

                    nameError = name.trim().isEmpty()
                    quantityError = parsedQty == null || parsedQty < 0
                    priceError = parsedPrice == null || parsedPrice < 0

                    if (!nameError && !quantityError && !priceError) {
                        onConfirm(name.trim(), sku.trim(), parsedQty!!, parsedPrice!!, category, description.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
