package br.com.devfocus.presentation.favorites

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.devfocus.presentation.components.RemoveFavoriteDialog
import br.com.devfocus.ui.theme.Background
import br.com.devfocus.ui.theme.Primary
import br.com.devfocus.ui.theme.Surface
import br.com.devfocus.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel
) {
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current
    var quoteToRemove by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Frases favoritas", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nenhuma frase favorita ainda.", color = Color.White, fontWeight = FontWeight.Medium)
                    Text(
                        "As frases que você curtir aparecerão aqui.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(favorites) { quote ->
                    FavoriteCard(
                        text = quote.text,
                        onRemove = { quoteToRemove = quote.id },
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "\"${quote.text}\"\n\n— DevFocus")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                    )
                }
            }
        }
    }

    if (quoteToRemove != null) {
        RemoveFavoriteDialog(
            onConfirm = {
                viewModel.toggleFavorite(quoteToRemove!!, true)
                quoteToRemove = null
            },
            onDismiss = { quoteToRemove = null }
        )
    }
}

@Composable
fun FavoriteCard(
    text: String,
    onRemove: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Favorite, contentDescription = "Remover favorito", tint = Primary)
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Compartilhar", tint = TextSecondary)
                }
            }
        }
    }
}

