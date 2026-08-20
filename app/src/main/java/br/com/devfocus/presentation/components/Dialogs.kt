package br.com.devfocus.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.devfocus.ui.theme.Primary
import br.com.devfocus.ui.theme.Surface
import br.com.devfocus.ui.theme.TextSecondary

@Composable
fun RemoveFavoriteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remover dos favoritos?", color = Color.White, fontWeight = FontWeight.Bold) },
        text = { Text("Tem certeza de que deseja remover esta frase das suas favoritas?", color = Color.White.copy(alpha = 0.7f)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Remover", color = Primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = Surface,
        shape = RoundedCornerShape(28.dp)
    )
}
