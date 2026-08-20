package br.com.devfocus.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.devfocus.R
import br.com.devfocus.data.local.database.DevFocusDatabase
import br.com.devfocus.data.local.preferences.DevFocusPreferences
import br.com.devfocus.domain.logic.StreakCalculator
import br.com.devfocus.ui.theme.Primary
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class DevFocusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = DevFocusDatabase.getDatabase(context)
        val preferences = DevFocusPreferences(context)
        
        val quoteId = preferences.dailyQuoteId.first()
        val quote = quoteId?.let { database.quoteDao().getQuoteById(it) }
        val history = database.studyDao().getAllStudyHistory().first()
        val currentStreak = StreakCalculator.calculateCurrentStreak(history, LocalDate.now())

        provideContent {
            DevFocusWidgetContent(
                quoteText = quote?.text ?: "Seu foco constrói seu futuro",
                streak = currentStreak
            )
        }
    }

    @Composable
    private fun DevFocusWidgetContent(
        quoteText: String,
        streak: Int
    ) {
        // Fundo escuro com leve tom de roxo e transparência (~90% opaco)
        val widgetBackground = Color(0xE60A0B14) 
        val streakLabel = if (streak == 1) "dia" else "dias"
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBackground))
                .cornerRadius(28.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak no topo centralizado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_streak_fire),
                        contentDescription = null,
                        modifier = GlanceModifier.size(20.dp)
                    )
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "$streak",
                        style = TextStyle(
                            color = ColorProvider(Primary),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = streakLabel,
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.5f)),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(16.dp))

                // Frase centralizada como elemento principal
                Text(
                    text = quoteText,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 4
                )
            }
        }
    }
}
