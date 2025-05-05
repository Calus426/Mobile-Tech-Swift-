package com.tarumt.techswift.Profile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.tarumt.techswift.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.exp

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private var previousUser: FirebaseUser? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var gender by mutableStateOf("")
        private set

    var address by mutableStateOf("")
        private set

    var postcode by mutableStateOf("")
        private set

    var state by mutableStateOf("")
        private set

    var profileAvatar by mutableStateOf("")
        private set

    val currentUser get() = auth.currentUser

    init {
        previousUser = auth.currentUser  // Initialize with current user
        setupAuthListener()
        getUserProfile()
    }

    //Setup Listener to monitor acc changes
    private fun setupAuthListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser

            when {
                // New user logged in or switched accounts
                currentUser != null && currentUser.uid != previousUser?.uid -> {
                    previousUser = currentUser
                    resetProfile()
                }

                // No change (ignore)
                else -> Unit
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }


    fun nameUpdate(it: String) {
        name = it
    }

    fun addressUpdate(it: String) {
        address = it
    }

    fun emailUpdate(it: String) {
        email = it
    }

    fun phoneUpdate(it: String) {
        phone = it
    }

    fun genderUpdate(it: String) {
        gender = it
    }

    fun postcodeUpdate(it: String) {
        postcode = it
    }

    fun stateUpdate(it: String) {
        state = it
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {

                _uiState.update { currentState ->
                    currentState.copy(
                        selectedImageUri = imageUri
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    //Update the full address in uistate
    fun updateFullAdress(fullAddress: String) {
        _uiState.update { currentState ->
            currentState.copy(
                fullAddress = fullAddress
            )
        }
    }

    //update the user changes on profile details and store in database
    fun updateProfileDetails(context: Context,onSuccess : () -> Unit) {
        var url: String = ""

        if (_uiState.value.selectedImageUri != null) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val selectedUri = _uiState.value.selectedImageUri ?: return

            val path = "profilePic/" + auth.currentUser?.uid //profilePic/blabla.jpg
            val imageRef = storageRef.child(path)

            val inputStream = context.contentResolver.openInputStream(selectedUri)
            inputStream?.let {
                imageRef.putStream(it)
                    .addOnSuccessListener {
                        imageRef.downloadUrl
                            .addOnSuccessListener { downloadUri ->
                                //After successful store in db, remove the old pic from db
                                url = downloadUri.toString()
                                    profileAvatar = url
                                    saveProfileInDB(url, context,onSuccess)

                                // optional, if you need to observe it
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Fail upload picture", Toast.LENGTH_SHORT).show()
                        Log.e("Image upload", it.toString())
                        saveProfileInDB(url, context,onSuccess)
                    }

            }

        }
        else{
            saveProfileInDB(url, context,onSuccess)
        }

    }

    private fun saveProfileInDB(url: String, context: Context,onSuccess : () -> Unit) {
        //update the current profile value.
        _uiState.update { currentState ->
            currentState.copy(
                updatedProfile = User(
                    name = name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    address1 = address,
                    postcode = postcode,
                    state = state,
                    fullAddress = _uiState.value.fullAddress,
                    profileAvatar = url.ifEmpty { profileAvatar }
                )
            )
        }
        //check the updated and ori different
        val updates = _uiState.value.oriProfile.getUpdatedFieldsMap(_uiState.value.updatedProfile)

        //update the profile details
        if (updates.isNotEmpty()) {
            currentUser?.let {
                Firebase.firestore.collection("users")
                    .document(it.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        //update the updated profile to ori and clear the updated for next time use
                        _uiState.update { currentState ->
                            currentState.copy(
                                //replace latest to oriProfile
                                oriProfile = _uiState.value.updatedProfile,
                                //clear the updatedProfile for next time usage.
                                updatedProfile = User(),
                                selectedImageUri = null
                            )
                        }
                        Toast.makeText(context, "Save Profile Sucessfully!", Toast.LENGTH_SHORT)
                            .show()
                        onSuccess()
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
        }
        else{
            Toast.makeText(context, "No changes made!", Toast.LENGTH_SHORT)
                .show()
            onSuccess()
        }
    }

    fun User.getUpdatedFieldsMap(updated: User): Map<String, Any> {
        return buildMap {
            if (updated.name.isNotEmpty() && updated.name != name) put("name", updated.name)
            if (updated.email.isNotEmpty() && updated.email != email) put("email", updated.email)
            if (updated.phone.isNotEmpty() && updated.phone != phone) put(
                "phoneNumber",
                updated.phone
            )
            if (updated.gender.isNotEmpty() && updated.gender != gender) put(
                "gender",
                updated.gender
            )
            if (updated.address1.isNotEmpty() && updated.address1 != address1) put(
                "address1",
                updated.address1
            )
            if (updated.postcode != postcode) put("postcode", updated.postcode)
            if (updated.state.isNotEmpty() && updated.state != state) put("state", updated.state)
            if (updated.fullAddress.isNotEmpty() && updated.fullAddress != fullAddress) put(
                "fullAddress",
                updated.fullAddress
            )
            if (updated.profileAvatar != profileAvatar) put(
                "profileAvatar",
                updated.profileAvatar
            )
        }
    }

    //Get user profile details from firebase
    fun getUserProfile() {
        currentUser?.let {
            Firebase.firestore.collection("users")
                .document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            // Set oriProfile to this user
                            _uiState.update { currentState ->
                                currentState.copy(
                                    oriProfile = it
                                )
                            }
                            name = it.name
                            email = it.email
                            phone = it.phone
                            gender = it.gender
                            address = it.address1
                            postcode = it.postcode
                            state = it.state
                            profileAvatar = it.profileAvatar

                            Log.d("Firestore", "Sucess to fetch user")
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Failed to fetch user", exception)
                }
        }
    }

    //Fetch and Pop suggestion from PlacesAPI(autocomplete) when users type address
    fun loadAddressSuggestion(context: Context) {

        val placesClient = Places.createClient(context)
        val token = AutocompleteSessionToken.newInstance()

        if (address.length > 3) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(address)
                .setCountries(listOf("MY"))
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val suggestions = response.autocompletePredictions.map {
                        it.getFullText(null).toString()
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            addressSuggestion = suggestions
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e("AddressAutocomplete", "Failed to load predictions", it)
                }
        } else {
            if (_uiState.value.addressSuggestion.isNotEmpty()) {
                _uiState.update { currentState ->
                    currentState.copy(
                        addressSuggestion = emptyList()
                    )
                }
            }
        }
    }

    //Fetch the address details from PlacesAPI(text search) and auto insert to address field.
    fun loadAndFillAddress(address: String, context: Context) {
        val placeFields = listOf(Place.Field.ADDRESS_COMPONENTS)

        val request = SearchByTextRequest
            .builder(address, placeFields)
            .setMaxResultCount(1)
            .build()

        val placesClient = Places.createClient(context) // make sure context is accessible
        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                val validPlace = response.places.firstOrNull { place ->
                    place.addressComponents?.asList()?.isNotEmpty() == true
                }

                validPlace?.addressComponents?.asList()?.let { addressComponents ->
                    // Process the address components and build a full address string
                    var fullAddress = ""
                    var postcode = ""
                    var state = ""
                    val builder = StringBuilder()
                    var foundLocality = false


                    for (component in addressComponents) {
                        if (!foundLocality) {
                            if (component.types.contains("locality")) {
                                foundLocality = true
                                continue // stop adding to fullAddress
                            }
                            builder.append("${component.name}, ")
                        }
                        if (component.types.contains("postal_code")) {
                            postcode = component.name
                        }

                        if (component.types.contains("locality") || component.types.contains("administrative_area_level_1")) {
                            state = component.name
                        }
                    }

                    // Log the results
                    fullAddress = builder.toString().trim().removeSuffix(",")
                    addressUpdate(fullAddress)
                    postcodeUpdate(postcode)
                    stateUpdate(state)
                } ?: run {
                    Log.e("AddressAutocomplete", "No valid address found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddressAutocomplete", "Failed to load predictions", exception)
            }
    }


    fun resetProfile() {
        // Clear all mutable states
        name = ""
        email = ""
        phone = ""
        gender = ""
        address = ""
        postcode = ""
        state = ""
        profileAvatar = ""

        // Reset UI state
        _uiState.update { ProfileUiState() }

        // Reload profile for new user
        getUserProfile()
    }


}