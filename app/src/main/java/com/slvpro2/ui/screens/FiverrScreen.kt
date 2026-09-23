package com.example.slvpro2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FiverrStartScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B3D20))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Start your Fiverr Freelancer Journey",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Shape your professional freedom and grow successfully. Being a Fiverr freelancer means going your own way while we have your back.",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Sign up now", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(80.dp))
        
        Text("Trusted by", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OMR REVIEWS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Google", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("bitkom", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("L'OREAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
