package org.alertpreparedness.platform.v2

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import org.alertpreparedness.platform.v1.BuildConfig

val db by lazy { FirebaseDatabase.getInstance().reference.child(BuildConfig.ROOT_NODE) }

fun DatabaseReference.asObservable(): Observable<DataSnapshot> {
    return Observable.create<DataSnapshot> {emitter ->
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                emitter.onNext(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                emitter.onError(databaseError.toException())
            }
        }

        addValueEventListener(valueEventListener)

        emitter.setCancellable {
            removeEventListener(valueEventListener)
        }
    }
}
