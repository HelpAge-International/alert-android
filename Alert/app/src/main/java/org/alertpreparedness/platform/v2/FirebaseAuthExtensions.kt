package org.alertpreparedness.platform.v2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable

object FirebaseAuthExtensions {

    fun getUserAuthState():Observable<Pair<Boolean, FirebaseUser?>>{
        return Observable.create<Pair<Boolean, FirebaseUser?>> { emitter ->
            val authStateListener = FirebaseAuth.AuthStateListener {authState ->
                emitter.onNext(Pair(authState.currentUser != null, authState.currentUser))
            }
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.addAuthStateListener(authStateListener)

            emitter.setCancellable {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
    }

    fun getLoggedInUser(): Observable<FirebaseUser> {
        return getUserAuthState()
                .filter { it.first }
                .map {
                    it.second!!
                }
    }

    fun getLoggedInUserId(): Observable<String> {
        return getLoggedInUser()
                .map { it.uid }
                .distinctUntilChanged()
                .share()
    }
}
