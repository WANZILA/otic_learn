import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase
): ViewModel() {

    var state by mutableStateOf(UiState())
        private set

    data class UiState(
        val loading: Boolean = false,
        val error: String? = null
    )

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(loading = true, error = null)
            runCatching { login(email, password) }
                .onSuccess { onSuccess() }
                .onFailure { state = state.copy(error = it.message) }
            state = state.copy(loading = false)
        }
    }
}
