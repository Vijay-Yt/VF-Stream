package com.example.data

import com.example.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MovieCatalog {
    val categories = listOf(
        "Bollywood",
        "Hollywood",
        "South Hindi Dubbed",
        "Web Series",
        "Anime",
        "Punjabi Movies"
    )

    val movies = mutableListOf(
        Movie(
            id = "bolly_1",
            title = "Jawan",
            description = "A high-octane action thriller which outlines the emotional journey of a man who is set to rectify the wrongs in the society. Armed with adrenaline, deep personal stakes, and incredible stunts, he aims to reclaim his family honor and fight massive crime rings.",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1000&q=80",
            category = "Bollywood",
            rating = 8.8,
            language = "Hindi",
            duration = "2h 45m",
            videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", // Reliable Mux HLS Test Stream
            releaseYear = 2023,
            isTrending = true,
            isLatest = true
        ),
        Movie(
            id = "bolly_2",
            title = "Pathaan",
            description = "An Indian house agent Pathaan takes on a massive private terror organization named Outfit X, led by Jim, who plans a lethal biological weapon threat against Delhi. Pathaan must navigate espionage, explosive shootouts, and global locations to secure the country.",
            posterUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=1000&q=80",
            category = "Bollywood",
            rating = 8.1,
            language = "Hindi",
            duration = "2h 26m",
            videoUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8", // Reliable Akamai HLS Sintel Stream
            releaseYear = 2023,
            isTrending = true,
            isLatest = false
        ),
        Movie(
            id = "holly_1",
            title = "Interstellar",
            description = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival. Moving across time, stellar dimensions, and massive black holes, they find themselves in an ultimate strive for cosmic survival.",
            posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1000&q=80",
            category = "Hollywood",
            rating = 9.2,
            language = "English",
            duration = "2h 49m",
            videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8", // Tears of Steel HLS URL
            releaseYear = 2014,
            isTrending = true,
            isLatest = false
        ),
        Movie(
            id = "holly_2",
            title = "Avatar: The Way of Water",
            description = "Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri and the army of the Na'vi race to protect their home.",
            posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=1000&q=80",
            category = "Hollywood",
            rating = 8.7,
            language = "English",
            duration = "3h 12m",
            videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", // HLS H264
            releaseYear = 2022,
            isTrending = false,
            isLatest = true
        ),
        Movie(
            id = "south_1",
            title = "Pushpa 2: The Rule",
            description = "The clash continues between Pushpa Raj and SP Bhanwar Singh Shekhawat. Pushpa, now a powerful lord over the massive red sandalwood smuggling network, continues to rule supreme with absolute action, style, and iconic charm.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1000&q=80",
            category = "South Hindi Dubbed",
            rating = 9.0,
            language = "Hindi Dubbed",
            duration = "2h 55m",
            videoUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            releaseYear = 2025,
            isTrending = true,
            isLatest = true,
            isPremium = true
        ),
        Movie(
            id = "south_2",
            title = "K.G.F: Chapter 3",
            description = "In the gold-laden lands of Kolar, Rocky remains the ultimate savior and ruler of the mines. When global empires and deep military plots threaten to dismantle his supremacy of the goldfields, Rocky rises to exact explosive response.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1000&q=80",
            category = "South Hindi Dubbed",
            rating = 8.9,
            language = "Hindi Dubbed",
            duration = "2h 50m",
            videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            releaseYear = 2026,
            isTrending = false,
            isLatest = true
        ),
        Movie(
            id = "series_1",
            title = "Mirzapur Season 4",
            description = "The throne of Mirzapur remains highly contested. Guddu Pandit, now absolute lord of the crime trade, must guard his power from Golu, Kaleen Bhaiya, and several rival factions ready to claim power by any bloody means necessary.",
            posterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=1000&q=80",
            category = "Web Series",
            rating = 9.1,
            language = "Hindi",
            duration = "10 Episodes",
            videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            releaseYear = 2025,
            isTrending = true,
            isLatest = true,
            isPremium = true
        ),
        Movie(
            id = "series_2",
            title = "Money Heist",
            description = "An unusual group of robbers attempt to carry out the most perfect heist in Spanish history - stealing 2.4 billion euros from the Royal Mint of Spain. Guided by the mastermind Professor, they navigate hostage standoffs and intense SWAT tactics.",
            posterUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=1000&q=80",
            category = "Web Series",
            rating = 8.6,
            language = "Hindi Dubbed",
            duration = "5 Seasons",
            videoUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            releaseYear = 2021,
            isTrending = false,
            isLatest = false
        ),
        Movie(
            id = "anime_1",
            title = "Demon Slayer: Infinity Castle",
            description = "The Demon Slayers plunge deeply into the shifting dimensions of Muzan's dread-filled Infinity Castle. Tanjaro, Nezuko, and the noble pillars face the Upper Rank demons in an ultimate battle for the future of humanity.",
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1000&q=80",
            category = "Anime",
            rating = 9.3,
            language = "Japanese / Hindi Sub",
            duration = "1h 55m",
            videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            releaseYear = 2025,
            isTrending = true,
            isLatest = true,
            isPremium = true
        ),
        Movie(
            id = "anime_2",
            title = "Naruto Shippuden: The Movie",
            description = "Naruto faces a dread prediction of his own demise if he continues his mission to guard the sacred priestess Shion against a colossal spiritual stone army. He must risk all on his ninja way to bypass fate.",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1000&q=80",
            category = "Anime",
            rating = 8.3,
            language = "Hindi Dubbed",
            duration = "1h 42m",
            videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            releaseYear = 2012,
            isTrending = false,
            isLatest = false
        ),
        Movie(
            id = "punj_1",
            title = "Carry On Jatta 3",
            description = "A hilarious sequence of comic errors and lies unfolds when Jass falls in love with Meet. To secure his marriage, he invents a crazy fake family, leaving his straight-laced father and crazy friends in unmatched chaos.",
            posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=1000&q=80",
            category = "Punjabi Movies",
            rating = 8.5,
            language = "Punjabi",
            duration = "2h 15m",
            videoUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            releaseYear = 2023,
            isTrending = true,
            isLatest = true
        ),
        Movie(
            id = "punj_2",
            title = "Jatt & Juliet 3",
            description = "Fate reunited the infamous Jatt and Juliet in Canada as state officers on a crucial joint diplomatic project. Spark and friction start fly heavily once again, leading to an incredible modern romantic laughter drama.",
            posterUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500&q=80",
            landscapeUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=1000&q=80",
            category = "Punjabi Movies",
            rating = 8.4,
            language = "Punjabi",
            duration = "2h 20m",
            videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            releaseYear = 2024,
            isTrending = false,
            isLatest = true
        )
    )

    private val _moviesFlow = MutableStateFlow<List<Movie>>(movies.toList())
    val moviesFlow: StateFlow<List<Movie>> = _moviesFlow.asStateFlow()

    fun initCatalog(context: android.content.Context) {
        val prefs = context.getSharedPreferences("movie_catalog_prefs", android.content.Context.MODE_PRIVATE)
        val moviesJson = prefs.getString("movies_list_json", null)
        if (moviesJson != null) {
            try {
                val jsonArray = org.json.JSONArray(moviesJson)
                val loadedMovies = mutableListOf<Movie>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    loadedMovies.add(
                        Movie(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            description = obj.getString("description"),
                            posterUrl = obj.getString("posterUrl"),
                            landscapeUrl = obj.getString("landscapeUrl"),
                            category = obj.getString("category"),
                            rating = obj.getDouble("rating"),
                            language = obj.getString("language"),
                            duration = obj.getString("duration"),
                            videoUrl = obj.getString("videoUrl"),
                            releaseYear = obj.getInt("releaseYear"),
                            isTrending = obj.optBoolean("isTrending", false),
                            isLatest = obj.optBoolean("isLatest", false),
                            isPremium = obj.optBoolean("isPremium", false)
                        )
                    )
                }
                movies.clear()
                movies.addAll(loadedMovies)
                _moviesFlow.value = movies.toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun persistCatalog(context: android.content.Context) {
        val prefs = context.getSharedPreferences("movie_catalog_prefs", android.content.Context.MODE_PRIVATE)
        try {
            val jsonArray = org.json.JSONArray()
            for (movie in movies) {
                val obj = org.json.JSONObject()
                obj.put("id", movie.id)
                obj.put("title", movie.title)
                obj.put("description", movie.description)
                obj.put("posterUrl", movie.posterUrl)
                obj.put("landscapeUrl", movie.landscapeUrl)
                obj.put("category", movie.category)
                obj.put("rating", movie.rating)
                obj.put("language", movie.language)
                obj.put("duration", movie.duration)
                obj.put("videoUrl", movie.videoUrl)
                obj.put("releaseYear", movie.releaseYear)
                obj.put("isTrending", movie.isTrending)
                obj.put("isLatest", movie.isLatest)
                obj.put("isPremium", movie.isPremium)
                jsonArray.put(obj)
            }
            prefs.edit().putString("movies_list_json", jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMovieById(id: String): Movie? {
        return movies.find { it.id == id }
    }

    fun addMovie(context: android.content.Context, movie: Movie) {
        movies.add(movie)
        _moviesFlow.value = movies.toList()
        persistCatalog(context)
    }

    fun updateMovie(context: android.content.Context, movie: Movie) {
        val index = movies.indexOfFirst { it.id == movie.id }
        if (index != -1) {
            movies[index] = movie
            _moviesFlow.value = movies.toList()
            persistCatalog(context)
        }
    }

    fun deleteMovie(context: android.content.Context, id: String) {
        movies.removeAll { it.id == id }
        _moviesFlow.value = movies.toList()
        persistCatalog(context)
    }
}
