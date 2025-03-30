package com.tarumt.techswift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.ui.theme.BottomBar
import com.tarumt.techswift.ui.theme.GreenBackground

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    val navItemList = listOf(
        NavItem("Home", R.drawable.homeselected,R.drawable.homenonselected,Navigation.Home.name),
        NavItem("History",R.drawable.orderselected,R.drawable.ordernonselected,Navigation.History.name)
    )

    var selectedButton by remember{ mutableIntStateOf(0)}

    Scaffold(
        containerColor = GreenBackground,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = BottomBar,
                modifier = Modifier.clip(RoundedCornerShape(24.dp))) {
                navItemList.forEachIndexed{index,navItem ->
                    NavigationBarItem(
                        selected = selectedButton == index,
                        onClick = {
                                navController.navigate(navItem.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                            selectedButton = index
                        },
                        icon = {
                            if(selectedButton == index){
                                Icon(painterResource(id = navItem.iconSelected), contentDescription = navItem.label)
                            }
                            else{
                                Icon(painterResource(id = navItem.iconNonSelected), contentDescription = navItem.label)
                            }
                        },
                        label = {
                            if (selectedButton == index) {
                                Text(text = navItem.label,
                                    color = Color.Black,
                                )
                            } else {
                                Text(text = "") // Hide label when not selected
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent // Remove selection effect
                        )
                    )
                }

            }
        }

    ) { innerPadding ->

        Navigate(navController = navController, Modifier.padding(innerPadding))
    }
}