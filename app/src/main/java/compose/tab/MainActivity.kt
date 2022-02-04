package compose.tab

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import compose.tab.ui.theme.ComposeTabTheme

class MainActivity : ComponentActivity() {
    sealed class Screen(val route: String) {
        object Screen1 : Screen("screen1")
        object Screen2 : Screen("screen2")
        object Screen3 : Screen("screen3")
    }

    val items = listOf(
        Screen.Screen1,
        Screen.Screen2,
        Screen.Screen3,
    )
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTabTheme {
                navController = rememberNavController()

                NavHost(navController = navController!!, startDestination = "home") {
                    composable(
                        "home", deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "app://compose.tab/" + Screen.Screen1.route
                            },
                            navDeepLink {
                                uriPattern = "app://compose.tab/" + Screen.Screen2.route
                            },
                            navDeepLink {
                                uriPattern = "app://compose.tab/" + Screen.Screen3.route
                            },
                        )
                    ) { HomeScreen() }
                    composable(
                        "bigscreen",
                        deepLinks = listOf(navDeepLink {
                            uriPattern = "app://compose.tab/bigscreen"
                        })
                    ) {
                        BigScreen(navController = navController!!)
                    }
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }

    @Composable
    fun HomeScreen() {
        // A surface container using the 'background' color from the theme
        val navController = rememberNavController()
        Scaffold(bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(screen.route) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Screen1.route,
                Modifier.padding(innerPadding)
            ) {
                composable(
                    Screen.Screen1.route,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "app://compose.tab/" + Screen.Screen1.route
                    })
                ) {
                    Greeting1(
                        navController
                    )
                }
                composable(Screen.Screen2.route,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "app://compose.tab/" + Screen.Screen2.route
                    })
                ) {
                    Greeting2(
                        navController
                    )
                }
                composable(Screen.Screen3.route,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "app://compose.tab/" + Screen.Screen3.route
                    })
                ) {
                    Greeting3(
                        navController
                    )
                }
            }
        }
    }

    @Composable
    fun Greeting1(navController: NavHostController) {
        Text(text = "Hello $!", color = Color.Blue)
    }


    @Composable
    fun Greeting2(navController: NavHostController) {
        Text(text = "Hello !", color = Color.Green)
    }


    @Composable
    fun Greeting3(navController: NavHostController) {
        val context = LocalContext.current
        Button(onClick = {
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                "app://compose.tab/bigscreen".toUri(),
                context,
                MainActivity::class.java
            )

            val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            context.startActivity(deepLinkIntent)
        }) {

            Text(text = "Hello $!", color = Color.Red)
        }
    }


    @Composable
    fun BigScreen(navController: NavHostController) {
        val context = LocalContext.current
        Button(onClick = {
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                "app://compose.tab/screen2".toUri(),
                context,
                MainActivity::class.java
            )

            val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            context.startActivity(deepLinkIntent)
        }) {
            Text(text = "Navigate", color = Color.Red)
        }

    }

//    @Preview(showBackground = true)
//    @Composable
//    fun DefaultPreview() {
//
//        val navController = rememberNavController()
//        ComposeTabTheme {
//            Greeting1(navController, Screen.Screen1.route)
//        }
//    }
}