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
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
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
                isFavorite = quote?.isFavorite ?: false,
                streak = currentStreak
            )
        }
    }

    @Composable
    private fun DevFocusWidgetContent(
        context: Context,
        quoteText: String,
        isFavorite: Boolean,
        streak: Int
    ) {
        // Cor escura com leve tom de roxo e transparência (~90% opaco / 10% transparente)
        val widgetBackground = Color(0xE60A0B14) 
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBackground))
                .padding(16.dp)
                .clickable(actionStartActivity(intent)),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "Dev",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Focus",
                        style = TextStyle(
                            color = ColorProvider(Primary),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.defaultWeight())
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥", 
                        style = TextStyle(color = ColorProvider(Primary), fontSize = 14.sp)
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = "$streak dias",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(16.dp))

            Text(
                text = quoteText,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 3
            )
            
            Spacer(modifier = GlanceModifier.defaultWeight())
            
            Row(
                modifier = GlanceModifier.fillMaxWidth(), 
                horizontalAlignment = Alignment.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFavorite) "♥" else "♡", 
                    style = TextStyle(color = ColorProvider(Primary), fontSize = 24.sp),
                    modifier = GlanceModifier.clickable(actionStartActivity(intent))
                )
            }
        }
    }
}
