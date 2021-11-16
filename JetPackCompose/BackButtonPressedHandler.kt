import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner

private val localBackPressedDispatcher =
    staticCompositionLocalOf<OnBackPressedDispatcherOwner?> { null }

private class BackButtonHandler(enable: Boolean) : OnBackPressedCallback(enable) {
    lateinit var action: () -> Unit
    override fun handleOnBackPressed() {
        action()
    }
}


@Composable
internal fun Handler(
    action: () -> Unit,
    enabled: Boolean = true
) {
    val dispatcher = (localBackPressedDispatcher.current ?: return).onBackPressedDispatcher
    val handler = remember {
        BackButtonHandler(enable = true)
    }

    DisposableEffect(dispatcher) {
        dispatcher.addCallback(handler)

        onDispose { handler.remove() }
    }
    
    LaunchedEffect(enabled) {
        handler.isEnabled = enabled
        handler.action = action
    }
}

// Call this function from a composable function to register an action when back button is pressed

@Composable
internal fun OnBackPressedHandler(
    action: () -> Unit,
) {
    CompositionLocalProvider(
        localBackPressedDispatcher provides LocalLifecycleOwner.current as ComponentActivity
    ) {
        Handler(action = action)
    }
}
