package com.tarumt.techswift

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.Model.NavItem
import com.tarumt.techswift.Profile.ProfileViewModel
import com.tarumt.techswift.ui.theme.BottomBar
import com.tarumt.techswift.ui.theme.GreenBackground
import com.tarumt.techswift.ui.theme.provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    windowInfo: WindowInfo
) {
    val authState = authViewModel.authState.observeAsState()
    val role = authViewModel.role.observeAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = navBackStackEntry?.destination?.route?.let { route ->
        Navigation.entries.firstOrNull { it.name == route }
    }

    val navItemList = listOf(
        NavItem(
            stringResource(R.string.home),
            R.drawable.homeselected,
            R.drawable.homenonselected,
            if (role.value == "U") Navigation.UserHome.name else Navigation.TechnicianHome.name
        ),
        NavItem(
            if(role.value == "U") stringResource(R.string.order) else stringResource(R.string.history) ,
            R.drawable.orderselected,
            R.drawable.ordernonselected,
            if (role.value == "U") Navigation.UserOrder.name else Navigation.TechnicianHistory.name
        )
    )

    val selectedButton = remember(currentRoute) {
        navItemList.firstOrNull { it.route == currentRoute }
    }


    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    val login = currentRoute != Navigation.Login.name



    if (authState.value == null || role.value == null) { //Loading
        return
    } else {
        val navViewModel: NavViewModel = viewModel()
        val profileViewModel: ProfileViewModel = viewModel()
        val uiState = profileViewModel.uiState.collectAsState()

        if (login) { //Check if is in login page

            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact ||
                windowInfo.screenHeightInfo is WindowInfo.WindowType.Compact
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = GreenBackground
                        ) {

                            DrawerContent(
                                navController,
                                closeDrawer = {
                                    scope.launch {
                                        drawerState.close()

                                    }
                                },
                                onLogoutClick = {
                                    authViewModel.signout()

                                },
                                uiState.value.oriProfile.profileAvatar,
                                uiState.value.oriProfile.name,
                                windowInfo = windowInfo,
                                role = role.value,
                            )

                        }
                    }
                ) {
                    CompactScaffold(
                        navItemList,
                        selectedButton,
                        currentRoute,
                        navController,
                        currentScreen,
                        scope,
                        drawerState,
                        authViewModel,
                        profileViewModel,
                        windowInfo,
                        navViewModel
                    )
                }

            } else {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet(drawerContainerColor = GreenBackground) {
                            DrawerContent(
                                navController,
                                closeDrawer = {}, // Not closable
                                onLogoutClick = { authViewModel.signout() },
                                profileAvatar = uiState.value.oriProfile.profileAvatar,
                                name = uiState.value.oriProfile.name,
                                windowInfo = windowInfo,
                                role = role.value
                            )
                        }
                    }
                ) {
                    TabletScaffold(
                        currentScreen,
                        navController,
                        scope,
                        drawerState,
                        authViewModel,
                        profileViewModel,
                        windowInfo,
                        navViewModel
                    )
                }
            }
        } else {
            Scaffold(
                containerColor = GreenBackground,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Navigate(
                    navController = navController,
                    modifier = Modifier
                        .padding(innerPadding),
                    authViewModel = authViewModel,
                    windowInfo = windowInfo
                )
            }
        }
    }
    if (drawerState.isOpen) {
        BackHandler {
            scope.launch {
                drawerState.close()
            }
        }
    }


}

@Composable
private fun TabletScaffold(
    currentScreen: Navigation?,
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    windowInfo: WindowInfo,
    navViewModel: NavViewModel
) {
    Scaffold(
        containerColor = GreenBackground,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarApp(
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() },
                onOpenDrawer = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                height = 65.dp,
                windowInfo = windowInfo
            )
        }

    ) { innerPadding ->
        Navigate(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .zIndex(1f),
            authViewModel = authViewModel,
            profileViewModel = profileViewModel,
            windowInfo = windowInfo,
            navViewModel = navViewModel
        )

    }
}

@Composable
private fun CompactScaffold(
    navItemList: List<NavItem>,
    selectedButton: NavItem?,
    currentRoute: String?,
    navController: NavHostController,
    currentScreen: Navigation?,
    scope: CoroutineScope,
    drawerState: DrawerState,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    windowInfo: WindowInfo,
    navViewModel: NavViewModel
) {
    Scaffold(
        containerColor = GreenBackground,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
                BottomNavigationBar(
                    navItemList,
                    selectedButton,
                    currentRoute,
                    navController
                )
            }
        },
        topBar = {
            TopBarApp(
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() },
                onOpenDrawer = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                height = 95.dp,
                windowInfo = windowInfo
            )
        }

    ) { innerPadding ->
        Navigate(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .zIndex(1f),
            authViewModel = authViewModel,
            profileViewModel = profileViewModel,
            windowInfo = windowInfo,
            navViewModel = navViewModel
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp(
    currentScreen: Navigation?,
    navigateUp: () -> Unit,
    onOpenDrawer: () -> Unit,
    height: Dp,
    windowInfo: WindowInfo
) {

    if (currentScreen != null && currentScreen != Navigation.Login) {
        val fontName = GoogleFont("Poppins")

        CenterAlignedTopAppBar(
            modifier = Modifier
                .height(height),
            title = {
                Box(
                    modifier = Modifier.fillMaxHeight(), // Takes full height of AppBar
                    contentAlignment = Alignment.Center // Centers the Text both ways
                ) {
                    Text(
                        text = stringResource(currentScreen.title),
                        color = Color.White,
                        fontFamily = FontFamily(
                            Font(
                                googleFont = fontName,
                                fontProvider = provider,
                                weight = FontWeight.SemiBold
                            )
                        ),
                        fontSize = 20.sp
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = GreenBackground
            ),
            navigationIcon = {

                if (currentScreen.canNavigate) {
                    IconButton(
                        onClick = navigateUp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = Color.White
                        )
                    }
                } else {
                    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact ||
                        windowInfo.screenHeightInfo is WindowInfo.WindowType.Compact
                    ){
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(45.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxHeight()
                                    .size(35.dp)
                            )
                        }
                    }

                }
            }
        )
    }


}

@Composable
private fun BottomNavigationBar(
    navItemList: List<NavItem>,
    selectedButton: NavItem?,
    currentRoute: String?,
    navController: NavHostController
) {
    if (currentRoute != Navigation.Profile.name) {
        NavigationBar(
            containerColor = BottomBar,
            modifier = Modifier.clip(RoundedCornerShape(25.dp))
        ) {
            navItemList.forEachIndexed { index, navItem ->
                NavigationBarItem(
                    selected = selectedButton?.route == navItem.route,
                    onClick = {
                        if (currentRoute != navItem.route) {
                            if (currentRoute == Navigation.ServiceDetails.name && navItem.route == Navigation.UserHome.name) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(navItem.route) {
                                    popUpTo(navItem.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                        }

                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (selectedButton?.route == navItem.route) navItem.iconSelected
                                else navItem.iconNonSelected
                            ),
                            contentDescription = navItem.label
                        )
                    },
                    label = {
                        if (selectedButton?.route == navItem.route) {
                            Text(text = navItem.label, color = Color.Black)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent // Remove selection effect
                    )
                )
            }

        }
    }

}

@Composable
fun DrawerContent(
    navController: NavHostController,
    closeDrawer: () -> Unit,
    onLogoutClick: () -> Unit,
    profileAvatar: String,
    name: String,
    role: String?,
    windowInfo: WindowInfo
) {

    Row(
        modifier = Modifier.padding(30.dp)
            .fillMaxWidth(0.6f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = profileAvatar, // Directly pass Firebase Storage reference
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = CircleShape
                ),
            placeholder = painterResource(R.drawable.default_avatar), // Show while loading
            error = painterResource(R.drawable.default_avatar) // Show if loading fails
        )

        Column(
            modifier = Modifier.padding(13.dp)

        ) {
            Text(
                text = "Hello!",
                fontSize = 16.sp,
                color = Color(0xFFBAB4B4).copy(alpha = 0.85f)
            )
            Text(
                text = name,
                fontSize = 28.sp,
                color = Color.White
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth(0.6f)  // Drawer takes full width
            .padding(start = 16.dp)
            .verticalScroll(rememberScrollState()) // Enable vertical scrolling
    ) {

        Text(
            text = "MAIN",
            fontSize = 10.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (windowInfo.screenWidthInfo !is WindowInfo.WindowType.Compact) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.home),
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.home),
                        color = Color.White,
                        fontSize = 25.sp
                    )
                },
                selected = false,
                onClick = {
                    val destination = when (role) {
                        "T" -> Navigation.TechnicianHome.name
                        "U" -> Navigation.UserHome.name
                        else -> Navigation.Login.name
                    }

                    navController.navigate(destination) {
                        popUpTo(destination) { inclusive = true }
                        launchSingleTop = true
                    }
                    closeDrawer()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.Transparent,  // No background on selection
                    unselectedContainerColor = Color.Transparent // No background when not selected
                ),
                shape = RoundedCornerShape(0.dp),// Remove rounded corners
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (role == "U") {
                NavigationDrawerItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.order_fill),
                            contentDescription = stringResource(R.string.order),
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.order),
                            color = Color.White,
                            fontSize = 25.sp
                        )
                    },
                    selected = false,
                    onClick = {
                        navController.navigate(Navigation.UserOrder.name) {
                            popUpTo(Navigation.UserOrder.name) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        closeDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.Transparent,  // No background on selection
                        unselectedContainerColor = Color.Transparent // No background when not selected
                    ),
                    shape = RoundedCornerShape(0.dp),// Remove rounded corners
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = stringResource(R.string.profile),
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.profile),
                    color = Color.White,
                    fontSize = 25.sp
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Navigation.Profile.name) {
                    popUpTo(Navigation.Profile.name) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                closeDrawer()
            },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Transparent,  // No background on selection
                unselectedContainerColor = Color.Transparent // No background when not selected
            ),
            shape = RoundedCornerShape(0.dp),// Remove rounded corners
        )
        Spacer(modifier = Modifier.height(10.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.round_history_24),
                    contentDescription = stringResource(R.string.history),
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.history),
                    color = Color.White,
                    fontSize = 25.sp
                )
            },
            selected = false,
            onClick = {
                val destination = when (role) {
                    "T" -> Navigation.TechnicianHistory.name
                    "U" -> Navigation.UserHistory.name
                    else -> Navigation.Login.name
                }

                navController.navigate(destination) {
                    popUpTo(destination) { inclusive = true }
                    launchSingleTop = true
                }
                closeDrawer()
            },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Transparent,  // No background on selection
                unselectedContainerColor = Color.Transparent // No background when not selected
            ),
            shape = RoundedCornerShape(0.dp),// Remove rounded corners
        )

        Spacer(modifier = Modifier.height(10.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.sign_out),
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 25.sp
                )
            },
            selected = false,
            onClick = {
                onLogoutClick()
                closeDrawer()
            },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Transparent,  // No background on selection
                unselectedContainerColor = Color.Transparent // No background when not selected
            ),
            shape = RoundedCornerShape(0.dp),// Remove rounded corners
        )
    }


}


