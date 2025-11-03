package com.android.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.android.giphy.presentation.detail.GiphyDetailRoute
import com.android.giphy.presentation.search.GiphySearchRoute
import com.android.navigation.NavigationManager.navigateTo
import com.android.screens.GiphyScreens

fun NavGraphBuilder.giphyNavigation(rootNavController: NavHostController) {
    composable<GiphyScreens.SearchScreen> {
        GiphySearchRoute(onNavigate = { screens -> rootNavController.navigateTo(screens) })
    }

    composable<GiphyScreens.DetailScreen> { backStackEntry ->
        val detail: GiphyScreens.DetailScreen = backStackEntry.toRoute()
        GiphyDetailRoute(
            gifId = detail.gifId,
            onNavigate = { screens -> rootNavController.navigateTo(screens) }
        )
    }
}