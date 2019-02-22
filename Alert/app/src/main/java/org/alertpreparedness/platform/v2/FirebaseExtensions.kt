package org.alertpreparedness.platform.v2

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import org.alertpreparedness.platform.v1.BuildConfig
import org.alertpreparedness.platform.v2.utils.extensions.gson

val db by lazy { FirebaseDatabase.getInstance().reference.child(BuildConfig.ROOT_NODE) }

fun DatabaseReference.asObservable(): Observable<DataSnapshot> {
    return Observable.create<DataSnapshot> {emitter ->
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    emitter.onNext(dataSnapshot)
                }
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

fun DatabaseReference.printRef(key: String = ""): DatabaseReference {
    println("$key - $ref")
    return this
}

fun DatabaseReference.setValueRx(value: Any?): Observable<Unit> {
    return Observable.create<Unit> { emitter ->
        setValue(gson.fromJson(gson.toJson(value), Any::class.java)) { databaseError, _ ->
            if (databaseError == null) {
                emitter.onNext(Unit)
                emitter.onComplete()
            } else {
                emitter.onError(databaseError.toException())
            }
        }
    }
}

fun DatabaseReference.updateChildrenRx(values: Map<String, Any?>): Observable<Unit> {
    return Observable.create<Unit> { emitter ->
        updateChildren(values) { databaseError, _ ->
            if (databaseError == null) {
                emitter.onNext(Unit)
                emitter.onComplete()
            } else {
                emitter.onError(databaseError.toException())
            }
        }
    }
}

