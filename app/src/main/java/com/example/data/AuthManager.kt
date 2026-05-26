package com.example.data

import android.content.Context
import android.content.SharedPreferences

data class UserAccount(
    val email: String,
    val name: String,
    val displayName: String,
    val membershipType: String,
    val avatarIndex: Int,
    val isBanned: Boolean
)

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cinestream_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PASSWORD_PREFIX = "pwd_"
    }

    fun getAllUsers(): List<UserAccount> {
        val allUsers = mutableListOf<UserAccount>()
        val allPrefs = prefs.all
        for ((key, _) in allPrefs) {
            if (key.startsWith(KEY_USER_PASSWORD_PREFIX)) {
                val email = key.substring(KEY_USER_PASSWORD_PREFIX.length)
                val userName = prefs.getString("user_${email}_user_name", "Movie Enthusiast") ?: "Movie Enthusiast"
                val displayName = prefs.getString("user_${email}_display_name", "") ?: ""
                val membershipType = prefs.getString("user_${email}_membership_type", "Free Plan") ?: "Free Plan"
                val avatarIndex = prefs.getInt("user_${email}_avatar_index", 0)
                val isBanned = prefs.getBoolean("user_${email}_is_banned", false)
                allUsers.add(
                    UserAccount(
                        email = email,
                        name = userName,
                        displayName = displayName,
                        membershipType = membershipType,
                        avatarIndex = avatarIndex,
                        isBanned = isBanned
                    )
                )
            }
        }
        if (allUsers.none { it.email == "bijaydas8588@gmail.com" }) {
            allUsers.add(
                0,
                UserAccount(
                    email = "bijaydas8588@gmail.com",
                    name = "Bijay Das",
                    displayName = "Administrator",
                    membershipType = "Premium VIP",
                    avatarIndex = 1,
                    isBanned = false
                )
            )
        }
        return allUsers
    }

    fun setBanned(email: String, isBanned: Boolean) {
        if (email == "bijaydas8588@gmail.com") return
        prefs.edit().putBoolean("user_${email}_is_banned", isBanned).apply()
    }

    fun setMembership(email: String, membershipType: String) {
        prefs.edit().putString("user_${email}_membership_type", membershipType).apply()
    }

    fun deleteAccount(email: String) {
        if (email == "bijaydas8588@gmail.com") return
        prefs.edit()
            .remove(KEY_USER_PASSWORD_PREFIX + email)
            .remove("user_${email}_user_name")
            .remove("user_${email}_display_name")
            .remove("user_${email}_membership_type")
            .remove("user_${email}_avatar_index")
            .remove("user_${email}_is_banned")
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getLoggedInUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getLoggedInUserName(): String {
        val email = getLoggedInUserEmail() ?: return "Movie Enthusiast"
        return prefs.getString("user_${email}_user_name", "Movie Enthusiast") ?: "Movie Enthusiast"
    }

    fun getLoggedInDisplayName(): String {
        val email = getLoggedInUserEmail() ?: return ""
        return prefs.getString("user_${email}_display_name", "") ?: ""
    }

    fun getMembershipType(): String {
        val email = getLoggedInUserEmail() ?: return "Free Plan"
        return prefs.getString("user_${email}_membership_type", "Free Plan") ?: "Free Plan"
    }

    fun getAvatarIndex(): Int {
        val email = getLoggedInUserEmail() ?: return 0
        return prefs.getInt("user_${email}_avatar_index", 0)
    }

    fun updateProfile(displayName: String, userName: String, avatarIndex: Int) {
        val email = getLoggedInUserEmail() ?: return
        prefs.edit()
            .putString("user_${email}_display_name", displayName)
            .putString("user_${email}_user_name", userName)
            .putInt("user_${email}_avatar_index", avatarIndex)
            .apply()
    }

    fun setMembershipType(type: String) {
        val email = getLoggedInUserEmail() ?: return
        prefs.edit()
            .putString("user_${email}_membership_type", type)
            .apply()
    }

    fun login(email: String, password: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return "Email and password cannot be empty."
        }
        if (prefs.getBoolean("user_${email}_is_banned", false)) {
            return "Unauthorized access. This account has been permanently suspended by security operations."
        }
        val registeredPassword = prefs.getString(KEY_USER_PASSWORD_PREFIX + email, null)
        if (registeredPassword == null) {
            // Self-register for testing convenience if account doesn't exist yet!
            signup(email, email.substringBefore("@"), password)
        } else if (registeredPassword != password) {
            return "Incorrect password."
        }
        
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        return null // success
    }

    fun signup(email: String, name: String, password: String): String? {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            return "All fields are required."
        }
        if (!email.contains("@")) {
            return "Please enter a valid email address."
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters."
        }

        // Write the passwords AND initialize profile keys keyed on user email
        prefs.edit()
            .putString(KEY_USER_PASSWORD_PREFIX + email, password)
            .putString("user_${email}_user_name", name)
            .putString("user_${email}_display_name", name)
            .putString("user_${email}_membership_type", "Free Plan")
            .putInt("user_${email}_avatar_index", 0)
            .apply()
        return null // success
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_USER_EMAIL, null)
            .apply()
    }
}
