package br.com.devfocus.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.devfocus.ui.theme.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Histórico", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Seu histórico de estudos aparecerá aqui.", color = Color.White)
        }
    }
}
