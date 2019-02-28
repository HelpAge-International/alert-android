package org.alertpreparedness.platform.v2

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import org.alertpreparedness.platform.v2.utils.extensions.gson
import org.alertpreparedness.platform.v2.utils.extensions.onDispose

fun DatabaseReference.asObservable(): Observable<DataSnapshot> {
    val stackTrace = Thread.currentThread().stackTrace.joinToString("\n")
    return Observable.create<DataSnapshot> {emitter ->
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!emitter.isDisposed && dataSnapshot.value != null) {
                    emitter.onNext(dataSnapshot)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (!emitter.isDisposed) {
                    printRef("Errored at: ")
                    println(stackTrace)
                    emitter.onError(databaseError.toException())
                }
            }
        }

        addValueEventListener(valueEventListener)

        emitter.onDispose {
            println("Removed value event listener")
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

