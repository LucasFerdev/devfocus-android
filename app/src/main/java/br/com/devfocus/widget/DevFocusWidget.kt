package br.com.devfocus.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.devfocus.MainActivity
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
                context = context,
                quoteText = quote?.text ?: "Seu foco constrói seu futuro",
                streak = currentStreak
            )
        }
    }

    @Composable
    private fun DevFocusWidgetContent(
        context: Context,
        quoteText: String,
        streak: Int
    ) {
        // Fundo premium: grafite muito escuro com leve toque de roxo e transparência
        val widgetBackground = Color(0xF20D0E19) 
        val streakLabel = if (streak == 1) "dia" else "dias"
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBackground))
                .cornerRadius(32.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clickable(actionStartActivity(intent)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak centralizado no topo com hierarquia de cores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$streak",
                        style = TextStyle(
                            color = ColorProvider(Primary),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = streakLabel,
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(12.dp))

                // Frase grande e centralizada como elemento principal
                Text(
                    text = quoteText,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 3
                )
            }
        }
    }
}
