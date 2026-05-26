package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuthManager
import com.example.data.MovieCatalog
import com.example.data.entity.MovieDownloadEntity
import com.example.data.entity.WatchHistoryEntity
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MovieRepository(db.downloadDao(), db.watchHistoryDao(), application)
    private val authManager = AuthManager(application)

    // Network Connectivity State
    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    fun retryConnection() {
        val connectivityManager = getApplication<Application>().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val activeNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
            _isNetworkAvailable.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            _isNetworkAvailable.value = true
        }
    }

    init {
        // Initialize Movie Catalog from Shared Preferences
        MovieCatalog.initCatalog(application)

        val connectivityManager = application.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        // Check initial state
        try {
            val activeNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
            _isNetworkAvailable.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            _isNetworkAvailable.value = true
        }

        try {
            connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    _isNetworkAvailable.value = false
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Auth States
    private val _isUserLoggedIn = MutableStateFlow(authManager.isLoggedIn())
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _currentUserName = MutableStateFlow(authManager.getLoggedInUserName())
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _userEmail = MutableStateFlow(authManager.getLoggedInUserEmail() ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _displayName = MutableStateFlow(authManager.getLoggedInDisplayName())
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _membershipType = MutableStateFlow(authManager.getMembershipType())
    val membershipType: StateFlow<String> = _membershipType.asStateFlow()

    private val _avatarIndex = MutableStateFlow(authManager.getAvatarIndex())
    val avatarIndex: StateFlow<Int> = _avatarIndex.asStateFlow()

    fun updateProfile(newDisplayName: String, newUserName: String, newAvatarIndex: Int) {
        authManager.updateProfile(newDisplayName, newUserName, newAvatarIndex)
        _displayName.value = newDisplayName
        _currentUserName.value = newUserName
        _avatarIndex.value = newAvatarIndex
    }

    fun upgradeToPremium() {
        authManager.setMembershipType("Premium VIP")
        _membershipType.value = "Premium VIP"
    }

    // Admin Panel User Management Flow & Functions
    private val _allUsersList = MutableStateFlow<List<com.example.data.UserAccount>>(emptyList())
    val allUsersList: StateFlow<List<com.example.data.UserAccount>> = _allUsersList.asStateFlow()

    fun refreshUsersList() {
        _allUsersList.value = authManager.getAllUsers()
    }

    fun setBanned(email: String, isBanned: Boolean) {
        authManager.setBanned(email, isBanned)
        refreshUsersList()
    }

    fun setMembership(email: String, membershipType: String) {
        authManager.setMembership(email, membershipType)
        refreshUsersList()
    }

    fun deleteAccount(email: String) {
        authManager.deleteAccount(email)
        refreshUsersList()
    }

    // Global admin metrics tracking
    val allGlobalDownloads: StateFlow<List<com.example.data.entity.MovieDownloadEntity>> = db.downloadDao()
        .getAllMovieDownloadsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGlobalWatchHistory: StateFlow<List<com.example.data.entity.WatchHistoryEntity>> = db.watchHistoryDao()
        .getAllWatchHistoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Movie catalogs standard lists
    val allMovies: StateFlow<List<Movie>> = MovieCatalog.moviesFlow
    val categories = MovieCatalog.categories

    // Dynamic, reactive streams mapping to the currently active user context
    val activeDownloads: StateFlow<List<MovieDownloadEntity>> = _userEmail
        .flatMapLatest { email ->
            if (email.isBlank()) flowOf(emptyList())
            else repository.getDownloadsForUser(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory: StateFlow<List<WatchHistoryEntity>> = _userEmail
        .flatMapLatest { email ->
            if (email.isBlank()) flowOf(emptyList())
            else repository.getWatchHistoryForUser(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filters states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val filteredMovies: StateFlow<List<Movie>> = combine(allMovies, _searchQuery, _selectedCategory) { movies, query, category ->
        var list: List<Movie> = movies
        if (category != null) {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MovieCatalog.movies.toList())

    // Selected Movie context (for playback & detail sheet)
    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    fun selectMovie(movie: Movie?) {
        _selectedMovie.value = movie
    }

    // Search trigger
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Category toggle
    fun toggleCategory(category: String?) {
        _selectedCategory.value = category
    }

    // AUTH ACTIONS
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val error = authManager.login(email, password)
            if (error == null) {
                _isUserLoggedIn.value = true
                _userEmail.value = authManager.getLoggedInUserEmail() ?: ""
                _currentUserName.value = authManager.getLoggedInUserName()
                _displayName.value = authManager.getLoggedInDisplayName()
                _membershipType.value = authManager.getMembershipType()
                _avatarIndex.value = authManager.getAvatarIndex()
                onSuccess()
            } else {
                onError(error)
            }
        }
    }

    fun signup(email: String, name: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val error = authManager.signup(email, name, password)
            if (error == null) {
                // Perform login immediately after signup
                authManager.login(email, password)
                _isUserLoggedIn.value = true
                _userEmail.value = authManager.getLoggedInUserEmail() ?: ""
                _currentUserName.value = authManager.getLoggedInUserName()
                _displayName.value = authManager.getLoggedInDisplayName()
                _membershipType.value = authManager.getMembershipType()
                _avatarIndex.value = authManager.getAvatarIndex()
                onSuccess()
            } else {
                onError(error)
            }
        }
    }

    fun logout() {
        authManager.logout()
        _isUserLoggedIn.value = false
        _userEmail.value = ""
        _currentUserName.value = "Movie Enthusiast"
        _displayName.value = ""
        _membershipType.value = "Free Plan"
        _avatarIndex.value = 0
    }

    // PLAYBACK WATCH HISTORY UPDATE
    fun updateMovieWatchPlayback(movie: Movie, positionMs: Long, durationMs: Long) {
        viewModelScope.launch {
            val email = _userEmail.value
            if (email.isNotBlank() && positionMs > 1000) { // Only track if watched more than 1 second
                repository.insertOrUpdateHistory(
                    movieId = movie.id,
                    userEmail = email,
                    title = movie.title,
                    category = movie.category,
                    posterUrl = movie.posterUrl,
                    positionMs = positionMs,
                    durationMs = durationMs
                )
            }
        }
    }

    fun deleteFromHistory(movieId: String) {
        viewModelScope.launch {
            val email = _userEmail.value
            if (email.isNotBlank()) {
                repository.deleteHistory(movieId, email)
            }
        }
    }

    // REAL DOWNLOAD ACTIONS
    fun startMovieDownload(movie: Movie) {
        val email = _userEmail.value
        if (email.isNotBlank()) {
            repository.startOrResumeDownload(viewModelScope, movie, email)
        }
    }

    fun pauseMovieDownload(movieId: String) {
        val email = _userEmail.value
        if (email.isNotBlank()) {
            repository.pauseDownload(movieId, email)
        }
    }

    fun deleteDownloadedMovie(movieId: String) {
        viewModelScope.launch {
            val email = _userEmail.value
            if (email.isNotBlank()) {
                repository.deleteDownload(movieId, email)
            }
        }
    }
}
