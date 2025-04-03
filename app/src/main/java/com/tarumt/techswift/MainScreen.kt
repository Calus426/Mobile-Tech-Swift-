package com.tarumt.techswift

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.ui.theme.BottomBar
import com.tarumt.techswift.ui.theme.GreenBackground

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItemList = listOf(
        NavItem(stringResource(R.string.home), R.drawable.homeselected,R.drawable.homenonselected,Navigation.Home.name),
        NavItem(stringResource(R.string.history),R.drawable.orderselected,R.drawable.ordernonselected,Navigation.History.name)
    )

    val selectedButton = remember(currentRoute) {
        navItemList.firstOrNull { it.route == currentRoute } ?: navItemList[0]
    }


    Scaffold(
        containerColor = GreenBackground,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = BottomBar,
                modifier = Modifier.clip(RoundedCornerShape(25.dp))) {
                navItemList.forEachIndexed{index,navItem ->
                    NavigationBarItem(
                        selected = selectedButton.route == navItem.route,
                        onClick = {
                            if (currentRoute != navItem.route){
                                if(currentRoute == Navigation.ServiceDetails.name && navItem.route == Navigation.Home.name)
                                {
                                    navController.popBackStack()
                                }
                                else{
                                    navController.navigate(navItem.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
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
                                    id = if (selectedButton.route == navItem.route) navItem.iconSelected
                                    else navItem.iconNonSelected
                                ),
                                contentDescription = navItem.label
                            )
                        },
                        label = {
                            if (selectedButton.route == navItem.route) {
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

    ) { innerPadding ->

        Navigate(navController = navController, Modifier.padding(innerPadding))
    }
}