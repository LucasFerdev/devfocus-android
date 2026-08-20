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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.devfocus.ui.theme.Primary

class DevFocusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            DevFocusWidgetContent()
        }
    }

    @Composable
    private fun DevFocusWidgetContent() {
        // Cor escura com leve tom de roxo e transparência (~90% opaco / 10% transparente)
        val widgetBackground = Color(0xE60A0B14) 
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBackground))
                .padding(16.dp),
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
                
                Text(
                    text = "🔥 12 dias",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            Text(
                text = "“",
                style = TextStyle(
                    color = ColorProvider(Primary),
                    fontSize = 48.sp, // Aumentado conforme solicitado
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "O código que você escreve hoje é o futuro que você constrói amanhã.",
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
                horizontalAlignment = Alignment.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "♥", 
                    style = TextStyle(color = ColorProvider(Primary), fontSize = 20.sp)
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                // Representação visual do botão de compartilhar
                Text(
                    text = "↗", 
                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.7f)), fontSize = 20.sp)
                )
            }
        }
    }
}
