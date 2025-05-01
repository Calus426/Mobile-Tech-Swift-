package com.tarumt.techswift.User.UiScreen.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.R
import com.tarumt.techswift.Model.Service
import com.tarumt.techswift.WindowInfo

@Composable
fun UserHomeUI(
    homeViewModel: UserHomeViewModel = viewModel(),
    onServiceClick: (Service) -> Unit,
    windowInfo: WindowInfo
) {


    val gridcells : Int
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){ //phone vertical
        gridcells = 2
    }
    else if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Medium
        && windowInfo.screenHeightInfo is WindowInfo.WindowType.Expanded){  //tablet vertical
        gridcells = 2
    }
    else{ 
        gridcells = 3
    }
    val homeUiState by homeViewModel.uiState.collectAsState()
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) 0.8f else 0.9f)
                .padding(1.dp),
            shape = RoundedCornerShape(30.dp), // Rounded corners
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

           Column{
               //Header
               Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .height(80.dp),
                   contentAlignment = Alignment.BottomCenter
               ){
                   Text(
                       text = stringResource(R.string.select_service),
                       style = MaterialTheme.typography.headlineSmall,
                       color = Color.Black,
                       fontSize = 24.sp
                   )

               }
           }

            //Service List
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridcells), // 2 columns grid
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalArrangement = Arrangement.SpaceAround,
                    contentPadding = PaddingValues(16.dp) // Optional: consistent padding
                ) {
                    items(homeUiState.listOfService.size){ index ->

                        ServiceItem(homeUiState.listOfService[index],onServiceClick=onServiceClick)
                    }
                }


        }

    }
}

@Composable
fun ServiceItem(service : Service, modifier: Modifier = Modifier,
                onServiceClick : (Service) -> Unit
){
    Column(
        modifier = Modifier
            .clickable { onServiceClick(service) }
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
            Image(
                painter = painterResource(id = service.image),
                contentDescription = stringResource(id = service.label),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = service.label),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontSize = 15.sp
            )
    }
}










