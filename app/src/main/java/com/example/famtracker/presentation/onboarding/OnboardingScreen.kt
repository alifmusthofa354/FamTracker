package com.example.famtracker.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.famtracker.R
import com.example.famtracker.data.preferences.OnboardingPreferences
import com.example.famtracker.presentation.MainScreenWrapper
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

data class OnboardingData(
    val title: String,
    val description: String,
    val imageRes: Int // Resource ID untuk gambar
)

class OnboardingScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: OnboardingViewModel = getViewModel()

        OnboardingContent(
            onFinish = {
                // Simpan status onboarding selesai
                viewModel.completeOnboarding()
                // Ganti ke MainScreen
                // navigator.replaceAll(MainScreenWrapper())
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingContent(onFinish: () -> Unit) {

    val pages = remember {
        listOf(
            OnboardingData(
                title = "Lacak Lokasi Keluarga\nSecara Real-Time",
                description = "Pantau posisi anggota keluarga Anda\nkapan saja, di mana saja dengan akurat",
                imageRes = R.drawable.onboarding_1
            ),
            OnboardingData(
                title = "Zona Aman untuk\nOrang Terkasih",
                description = "Buat zona aman dan dapatkan notifikasi\nsaat keluarga tiba atau meninggalkan lokasi",
                imageRes = R.drawable.onboarding_2
            ),
            OnboardingData(
                title = "Riwayat Perjalanan\nLengkap",
                description = "Lihat riwayat lokasi dan perjalanan\nkeluarga Anda hingga 30 hari terakhir",
                imageRes = R.drawable.onboarding_3
            )
        )
    }

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp)
            ) {
                if (!isLastPage) {
                    TextButton(
                        onClick = onFinish,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = "Lewati",
                            color = Color(0xFF6B7280),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(page = pages[page])
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    PageIndicator(isSelected = pagerState.currentPage == index)
                    if (index < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text(
                    text = if (isLastPage) "Mulai Sekarang" else "Selanjutnya",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun OnboardingPage(page: OnboardingData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                // Gambar ilustrasi
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = page.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun PageIndicator(isSelected: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 8.dp,
        label = "indicator"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF4A90E2) else Color(0xFFD1D5DB))
    )
}