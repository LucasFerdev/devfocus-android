package br.com.devfocus.presentation.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.devfocus.data.local.entity.QuoteEntity
import br.com.devfocus.data.local.entity.StudyStatus
import br.com.devfocus.domain.model.StudyDay
import br.com.devfocus.presentation.components.RemoveFavoriteDialog
import br.com.devfocus.ui.theme.*
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSeeAllClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showRemoveDialog by remember { mutableStateOf(false) }

    HomeScreenContent(
        uiState = uiState,
        onStudyClicked = viewModel::onStudyTodayClicked,
        onFavoriteClicked = { id, isFavorite ->
            if (isFavorite) {
                showRemoveDialog = true
            } else {
                viewModel.onFavoriteClicked(id, isFavorite)
            }
        },
        onUseFreezeClicked = viewModel::onUseFreezeClicked,
        onShareClicked = { text ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "\"$text\"\n\n— DevFocus")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },
        onSeeAllClicked = onSeeAllClicked
    )

    if (showRemoveDialog) {
        RemoveFavoriteDialog(
            onConfirm = {
                uiState.quote?.let { viewModel.onFavoriteClicked(it.id, it.isFavorite) }
                showRemoveDialog = false
            },
            onDismiss = { showRemoveDialog = false }
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onStudyClicked: () -> Unit,
    onFavoriteClicked: (Long, Boolean) -> Unit,
    onUseFreezeClicked: () -> Unit,
    onShareClicked: (String) -> Unit,
    onSeeAllClicked: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            HomeTopBar()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                QuoteCard(
                    quote = uiState.quote,
                    onFavoriteClicked = onFavoriteClicked,
                    onShareClicked = onShareClicked
                )
            }

            item {
                StreakCard(
                    currentStreak = uiState.currentStreak,
                    bestStreak = uiState.bestStreak
                )
            }

            item {
                WeeklyCalendar(
                    days = uiState.weekDays,
                    onStudyClicked = onStudyClicked,
                    studiedToday = uiState.studiedToday
                )
            }

            if (uiState.missedYesterday && uiState.freezeAvailable) {
                item {
                    FreezeBanner(onUseFreezeClicked = onUseFreezeClicked)
                }
            }
            
            item {
                FavoritesHeader(onSeeAllClicked = onSeeAllClicked)
            }
            
            items(uiState.favoriteQuotes) { quote ->
                FavoritePreviewCard(quote = quote.text)
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append("Dev")
                withStyle(style = SpanStyle(color = Primary)) {
                    append("Focus")
                }
            },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            ),
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Seu foco constrói seu futuro",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun QuoteCard(
    quote: QuoteEntity?,
    onFavoriteClicked: (Long, Boolean) -> Unit,
    onShareClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Frase do dia",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = quote?.text ?: "Carregando...",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    quote?.let { onFavoriteClicked(it.id, it.isFavorite) }
                }) {
                    Icon(
                        imageVector = if (quote?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Primary
                    )
                }
                
                IconButton(onClick = { 
                    quote?.let { onShareClicked(it.text) }
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun StreakCard(currentStreak: Int, bestStreak: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🔥 Sequência atual",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentStreak dias",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Melhor",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$bestStreak",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyCalendar(
    days: List<StudyDay>,
    onStudyClicked: () -> Unit,
    studiedToday: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val weekLabels = listOf("SEG", "TER", "QUA", "QUI", "SEX", "SÁB", "DOM")
            if (days.size == 7) {
                days.forEachIndexed { index, day ->
                    DayItem(label = weekLabels[index], day = day)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onStudyClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (studiedToday) SurfaceVariant else Primary,
                disabledContainerColor = SurfaceVariant
            ),
            shape = RoundedCornerShape(20.dp),
            enabled = !studiedToday
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (studiedToday) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (studiedToday) "Estudo registrado" else "Estudei hoje",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun DayItem(label: String, day: StudyDay) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        day.status == StudyStatus.STUDIED -> Primary
                        day.status == StudyStatus.FROZEN -> Color.White.copy(alpha = 0.1f)
                        day.isToday -> Color.Transparent
                        else -> Color.White.copy(alpha = 0.05f)
                    }
                )
                .then(
                    if (day.isToday && day.status == null) {
                        Modifier.border(1.dp, Primary, CircleShape)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                day.status == StudyStatus.STUDIED -> Icon(Icons.Default.Check, null, Modifier.size(20.dp), Color.White)
                day.status == StudyStatus.FROZEN -> Icon(Icons.Default.AcUnit, null, Modifier.size(20.dp), Primary)
                else -> {}
            }
        }
    }
}

@Composable
fun FreezeBanner(onUseFreezeClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AcUnit, null, tint = Primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Você não registrou estudo ontem.", color = Color.White, fontSize = 14.sp)
                TextButton(onClick = onUseFreezeClicked, contentPadding = PaddingValues(0.dp)) {
                    Text("❄ Usar Streak Freeze", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FavoritesHeader(onSeeAllClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Frases favoritas", color = Color.White, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAllClicked, contentPadding = PaddingValues(0.dp)) {
            Text("Ver todas", color = Primary, fontSize = 14.sp)
        }
    }
}

@Composable
fun FavoritePreviewCard(quote: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quote,
                color = Color.White,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.Favorite, null, tint = Primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    DevFocusTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                quote = QuoteEntity(text = "O sucesso é a soma de pequenos esforços repetidos dia após dia."),
                currentStreak = 12,
                bestStreak = 21,
                weekDays = (0..6).map { 
                    StudyDay(
                        date = LocalDate.now().minusDays(3 - it.toLong()),
                        status = if (it < 3) StudyStatus.STUDIED else null,
                        isToday = it == 3
                    )
                },
                isLoading = false
            ),
            onStudyClicked = {},
            onFavoriteClicked = { _, _ -> },
            onUseFreezeClicked = {},
            onShareClicked = {},
            onSeeAllClicked = {}
        )
    }
}
