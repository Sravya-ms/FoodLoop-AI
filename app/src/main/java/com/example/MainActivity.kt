package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.FoodLoopRepository
import com.example.ui.components.FoodLoopBottomNavigation
import com.example.ui.components.FoodLoopTopBar
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FoodLoopViewModel
import com.example.localization.LocalAppStrings
import com.example.localization.LanguageSelectionDialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = FoodLoopRepository(db)

        setContent {
            MyApplicationTheme {
                val viewModel: FoodLoopViewModel = viewModel {
                    FoodLoopViewModel(repository)
                }
                FoodLoopApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FoodLoopApp(viewModel: FoodLoopViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val userFeedback by viewModel.userFeedbackMessage.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentStrings by viewModel.currentStrings.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showRoleSwitcher by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val unreadNotifs = remember(notifications) {
        notifications.count { !it.isRead }
    }

    // Feedback snackbar
    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val canNavigateBack = remember(currentScreen, currentUser) {
        if (currentUser != null) {
            currentScreen != AppScreen.DONOR_DASHBOARD &&
            currentScreen != AppScreen.CONSUMER_DASHBOARD &&
            currentScreen != AppScreen.ADMIN_DASHBOARD
        } else {
            currentScreen != AppScreen.LANDING
        }
    }

    // System Back Handler - cannot pop to Home when logged in
    BackHandler(enabled = canNavigateBack) {
        viewModel.navigateBack()
    }

    CompositionLocalProvider(LocalAppStrings provides currentStrings) {
        Scaffold(
            topBar = {
                FoodLoopTopBar(
                    title = currentScreen.title,
                    currentUser = currentUser,
                    unreadNotifCount = unreadNotifs,
                    canNavigateBack = canNavigateBack,
                    onBackClick = { viewModel.navigateBack() },
                    onRoleSwitchClick = { showRoleSwitcher = true },
                    onNotificationsClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                    onAiAssistantClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                    onSignOutClick = { viewModel.logout() },
                    currentLanguage = currentLanguage,
                    onLanguageClick = { showLanguageDialog = true }
                )
            },
        bottomBar = {
            FoodLoopBottomNavigation(
                currentScreen = currentScreen,
                userRole = currentUser?.role,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.LANDING -> LandingScreen(viewModel = viewModel)
                    AppScreen.LOGIN -> AuthGatewayScreen(viewModel = viewModel, isRegister = false)
                    AppScreen.REGISTER -> AuthGatewayScreen(viewModel = viewModel, isRegister = true)
                    AppScreen.DONOR_LOGIN, AppScreen.DONOR_AUTH -> DonorLoginScreen(viewModel = viewModel)
                    AppScreen.DONOR_REGISTER -> DonorRegisterScreen(viewModel = viewModel)
                    AppScreen.CONSUMER_LOGIN, AppScreen.CONSUMER_AUTH -> ConsumerLoginScreen(viewModel = viewModel)
                    AppScreen.CONSUMER_REGISTER -> ConsumerRegisterScreen(viewModel = viewModel)
                    AppScreen.ADMIN_AUTH -> AdminAuthScreen(viewModel = viewModel)
                    AppScreen.DONOR_DASHBOARD -> DonorDashboardScreen(viewModel = viewModel)
                    AppScreen.DONATE_FOOD -> DonateFoodScreen(viewModel = viewModel)
                    AppScreen.DONATION_PREVIEW -> DonateFoodScreen(viewModel = viewModel)
                    AppScreen.CONSUMER_DASHBOARD -> ConsumerDashboardScreen(viewModel = viewModel)
                    AppScreen.DONATION_DETAILS -> DonationDetailsScreen(viewModel = viewModel)
                    AppScreen.ORDER_TRACKING -> OrderTrackingScreen(viewModel = viewModel)
                    AppScreen.COORDINATION_CHAT -> CoordinationChatScreen(viewModel = viewModel)
                    AppScreen.THANK_YOU -> ThankYouScreen(viewModel = viewModel)
                    AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                    AppScreen.ADMIN_VERIFICATION -> AdminVerificationScreen(viewModel = viewModel)
                    AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                    AppScreen.DONOR_PROFILE, AppScreen.CONSUMER_PROFILE -> ProfileScreen(viewModel = viewModel)
                    AppScreen.IMPACT_DASHBOARD -> ImpactDashboardScreen(viewModel = viewModel)
                    AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                }
            }
        }

        if (showRoleSwitcher) {
            RoleSwitcherDialog(
                currentRole = currentUser?.role,
                onRoleSelected = { role ->
                    viewModel.switchRole(role)
                    when (role) {
                        com.example.data.model.UserRole.DONOR -> viewModel.navigateTo(AppScreen.DONOR_DASHBOARD)
                        com.example.data.model.UserRole.CONSUMER -> viewModel.navigateTo(AppScreen.CONSUMER_DASHBOARD)
                        com.example.data.model.UserRole.ADMIN -> viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                    }
                },
                onSignOut = {
                    viewModel.logout()
                    viewModel.navigateTo(AppScreen.LOGIN)
                },
                onDismiss = { showRoleSwitcher = false }
            )
        }

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguageCode = currentLanguage.code,
                onLanguageSelected = { lang ->
                    viewModel.setLanguage(lang)
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}
}
