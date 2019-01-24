package org.alertpreparedness.platform.v2

import com.google.firebase.database.*
import io.reactivex.Observable

object FirebaseExtensions {
    val databaseReference = FirebaseDatabase.getInstance().reference
}


fun DatabaseReference.toObservable(): Observable<DataSnapshot> {
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
