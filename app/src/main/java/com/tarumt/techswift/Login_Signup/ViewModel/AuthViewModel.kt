package com.tarumt.techswift.Login_Signup.ViewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.Model.User

class AuthViewModel: ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    private val _role = MutableLiveData<String>()
    val role : LiveData<String> = _role

    init{
        checkAuthStatus()
    }
    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
            _role.value = ""
        }else
        {
            _authState.value = AuthState.Loading
            fetchUserRole()
        }

    }

    fun login(email : String, password : String){
        _authState.value = AuthState.Loading

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password cannot be empty!")

        }
        else{

            auth.signInWithEmailAndPassword(email.trim(),password.trim())
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        fetchUserRole()
                    }
                    else{
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Something went wrong.")
                    }
                }
        }

    }

    fun signup(email : String, password : String, currentUser : User, context : Context, onSuccess: () -> Unit = {}){
        _authState.value = AuthState.Loading

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password cannot be empty!")
        }
        else{
            auth.createUserWithEmailAndPassword(email.trim(),password.trim())
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        firestore.collection("users")
                            .document(uid)
                            .set(currentUser)
                            .addOnSuccessListener { Toast.makeText(context, "User data saved!", Toast.LENGTH_SHORT).show() }

                        fetchUserRole()
                        onSuccess()
                    }
                    else{
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Something went wrong.")
                    }
                }
        }

    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun fetchUserRole(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val userRole = document.getString("role") ?: "U"
                _role.value = userRole
                _authState.value = AuthState.Authenticated
            }
    }
}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}