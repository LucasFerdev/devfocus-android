package br.com.devfocus.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.devfocus.data.local.database.DevFocusDatabase
import br.com.devfocus.data.local.preferences.DevFocusPreferences
import br.com.devfocus.domain.logic.StreakCalculator
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
        // Cor escura com leve tom de roxo e transparência (~90% opaco / 10% transparente)
        val widgetBackground = Color(0xE60A0B14) 
        val streakLabel = if (streak == 1) "1 dia" else "$streak dias"
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBackground))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = streakLabel,
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = quoteText,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 4
                )
            }
        }
    }
}
