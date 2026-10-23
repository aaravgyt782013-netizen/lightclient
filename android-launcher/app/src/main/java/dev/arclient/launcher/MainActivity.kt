package dev.arclient.launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ArBackground = Color(0xFF090D18)
private val ArPanel = Color(0xFF151D2D)
private val ArBlue = Color(0xFF6D8DFF)
private val ArMint = Color(0xFF62E6C5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = ArBlue,
                secondary = ArMint,
                background = ArBackground,
                surface = ArPanel
            )) {
                ArHomeScreen(
                    onPlay = {
                        Toast.makeText(
                            this,
                            "Minecraft runtime integration is the next milestone.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onFeature = { feature ->
                        Toast.makeText(this, "$feature will be available in a later milestone.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun ArHomeScreen(
    onPlay: () -> Unit,
    onFeature: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = ArBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(ArBlue, ArMint)),
                            RoundedCornerShape(15.dp)
                        )
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.Gamepad, contentDescription = null, tint = ArBackground)
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text("AR CLIENT", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("JAVA EDITION • ANDROID", color = Color(0xFF9BAAC5), fontSize = 11.sp, letterSpacing = 1.sp)
                }
                Button(
                    onClick = { onFeature("Settings") },
                    colors = ButtonDefaults.buttonColors(containerColor = ArPanel)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF1D2C4A), Color(0xFF102C34))),
                        RoundedCornerShape(26.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("YOUR WORLD. YOUR WAY.", color = ArMint, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("Ready when
you are.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "A touch-first Java Edition launcher experience, built for your phone.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArMint, contentColor = ArBackground)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Text("  PLAY MINECRAFT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
                Text("Runtime setup is not connected yet.", color = Color(0xFF9BAAC5), fontSize = 11.sp)
            }

            Text("QUICK ACCESS", color = Color(0xFF9BAAC5), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.Extension, contentDescription = null, tint = ArMint) },
                    title = "Profiles",
                    subtitle = "Versions & mods",
                    onClick = { onFeature("Profiles") }
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.Cloud, contentDescription = null, tint = ArBlue) },
                    title = "Servers",
                    subtitle = "Your favourites",
                    onClick = { onFeature("Servers") }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("WHAT'S NEW", color = Color(0xFF9BAAC5), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Spacer(Modifier.weight(1f))
                Text("v0.1.0 • PREVIEW", color = ArMint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = ArPanel),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("The foundation is in place", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Ar Client is starting with a clean launcher shell and a separate Fabric HUD mod. Runtime integration and account sign-in are planned next.",
                        color = Color(0xFFB5C1D5),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
            Text(
                "Independent community project • Not affiliated with Mojang or Microsoft",
                color = Color(0xFF66738B),
                fontSize = 10.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = ArPanel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            icon()
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = Color(0xFF9BAAC5), fontSize = 12.sp)
        }
    }
}
