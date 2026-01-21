package com.epn.expensetracker.ui


import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epn.expensetracker.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    // Estado para la animación de escala (zoom)
    val scale = remember { Animatable(0f) }

    // Lógica de la animación
    LaunchedEffect(key1 = true) {
        // 1. Animamos la escala de 0 a 0.9 con un efecto de rebote (Overshoot)
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        // 2. Esperamos un poco para que el usuario vea el logo (1.5 segundos)
        delay(1500L)

        // 3. Avisamos que terminó para cambiar de pantalla
        onAnimationFinished()
    }

    // Diseño de la pantalla
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer) // Color de fondo elegante
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen con la animación de escala aplicada
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // CAMBIA ESTO POR TU LOGO
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale.value) // Aquí se aplica la animación
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Título mejorado
            Text(
                text = "Expense Tracker",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                // Aplicamos la misma escala para que aparezca con el logo
                modifier = Modifier.scale(scale.value)
            )

            Text(
                text = "Controla tus finanzas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}