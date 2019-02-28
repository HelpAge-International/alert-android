package org.alertpreparedness.platform.v2.utils.extensions

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import org.jetbrains.anko.imageResource

/**
 * Calls Observable.combineLatest() on [this] and [other] creating an [Observable] of Pairs of both
 * @return combination [Observable] of Pair<T, O1>.
 */

fun <T, O1> Observable<T>.combineWithPair(other: Observable<O1>): Observable<Pair<T, O1>> {
    return Observable.combineLatest(this, other, CombinePair())
}

/**
 * Calls Observable.zip() on [this] and [other] creating an [Observable] of Pairs of both
 * @return combination [Observable] of Pair<T, O1>.
 */
fun <T, O1> Observable<T>.zipWithPair(other: Observable<O1>): Observable<Pair<T, O1>> {
    return Observable.zip(this, other, CombinePair())
}

/**
 * Calls Observable.zip() on [this] and [other] creating an [Observable] of Pairs of both
 * @return combination [Observable] of Pair<T, O1>.
 */
fun <T, O1, O2> Observable<T>.zipWithTriple(other: Observable<O1>,
        other2: Observable<O2>): Observable<Triple<T, O1, O2>> {
    return Observable.zip(this, other, other2, CombineTriple())
}

/**
 * Calls Observable.zip() on [this] and [other] creating an [Observable] of Pairs of both
 * @return combination [Observable] of Pair<T, O1>.
 */
fun <T, O1, O2> Observable<T>.combineWithTriple(other: Observable<O1>,
        other2: Observable<O2>): Observable<Triple<T, O1, O2>> {
    return Observable.combineLatest(this, other, other2, CombineTriple())
}

/**
 * prints a value when onNext is called on the current [Observable] with optional [key] and [transform] of the value
 * @return the original unchanged [Observable]
 */
fun <T> Observable<T>.print(key: String = "it", transform: Function1<T, Any?> = { it }): Observable<T> {
    return doOnNext { println("$key = ${transform.invoke(it)}") }
}

fun <T1, T2, O1> Observable<Pair<T1, T2>>.mapFirst(function: Function1<T1, O1>): Observable<Pair<O1, T2>> {
    return this.map { (t1, t2) ->
        Pair(function(t1), t2)
    }
}

fun <T1, T2, O2> Observable<Pair<T1, T2>>.mapSecond(function: Function1<T2, O2>): Observable<Pair<T1, O2>> {
    return this.map { (t1, t2) ->
        Pair(t1, function(t2))
    }
}

/**
 * applies [function] to each value on the stream, throwing away null transformations
 * @return [Observable] of name [O1]
 */
fun <T, O1> Observable<T>.filterMap(function: Function1<T, O1?>): Observable<O1> {
    return flatMap { it ->
        val result = function.invoke(it)
        if (result != null) Observable.just(result) else Observable.empty()
    }
}

/**
 * converts materialized [Observable] back into regular stream, ignoring errors
 * @return [Observable] of the Values within the [Notification] name
 */
fun <T> Observable<Notification<T>>.removeError(): Observable<T> {
    return filterMap { notification ->
        if (notification.isOnNext) {
            notification.value
        } else {
            null
        }
    }
}

/**
 * Converts materialized [Observable] into a [Observable] of [Throwable]'s
 * @return [Observable] of [Throwable]'s thrown by the original [Observable]
 */
fun <T> Observable<Notification<T>>.toErrorObservable(): Observable<Throwable> {
    return filter { it.isOnError }
            .map { it.error }
}

/**
 * Emits the last value on the current stream when [toListen] emits a new item
 * @return [Observable] of [T]
 */
fun <T, O1> Observable<T>.takeWhen(toListen: Observable<O1>): Observable<T> {
    return toListen.withLatestFrom(this, BiFunction<O1, T, T> { _, T1 -> T1 })
}

/**
 * Emits the last value on the current stream when [toListen] emits a new item
 * @return [Observable] of [T]
 */
fun <T, O1> Observable<T>.takeIf(other: Observable<O1>, func: (O1) -> Boolean): Observable<T> {
    return this.withLatestFromPair(other)
            .filter { func(it.second) }
            .map { it.first }
}

/**
 * Emits the last value on the current stream when either a new value is emitted ot the current stream, or [toListen] emits a new item
 * @return [Observable] of [T]
 */
fun <T, O1> Observable<T>.takeWhenEither(toListen: Observable<O1>): Observable<T> {
    return Observable.combineLatest(this, toListen, BiFunction<T, O1, T> { T1, _ -> T1 })
}

/**
 * Calls withLatest on current observable and [other] and combines into a [Pair]
 * @return combination observable of Pair<T, O1>.
 */
fun <T, O1> Observable<T>.withLatestFromPair(other: Observable<O1>): Observable<Pair<T, O1>> {
    return this.withLatestFrom(other, CombinePair<T, O1>())
}

/**
 * Calls withLatest on current observable, [other1] and [other2] and combines into a [Triple]
 * @return combination observable of Triple<T, O1, O2>.
 */
fun <T, O1, O2> Observable<T>.withLatestFromTriple(other1: Observable<O1>,
        other2: Observable<O2>): Observable<Triple<T, O1, O2>> {
    return this.withLatestFrom(other1, other2, CombineTriple<T, O1, O2>())
}

/**
 * [BiFunction] that takes as input [T1] and [T2] and outputs a [Pair]
 */
class CombinePair<T1, T2> : BiFunction<T1, T2, Pair<T1, T2>> {

    override fun apply(t1: T1, t2: T2): Pair<T1, T2> {
        return Pair(t1, t2)
    }
}

/**
 * [BiFunction] that takes as input [T1], [T2] and [T3] and outputs a [Triple]
 */
class CombineTriple<T1, T2, T3> : Function3<T1, T2, T3, Triple<T1, T2, T3>> {

    override fun apply(t1: T1, t2: T2, t3: T3): Triple<T1, T2, T3> {
        return Triple(t1, t2, t3)
    }
}

class ListConcat<T> : BiFunction<List<T>, List<T>, List<T>> {
    override fun apply(list1: List<T>, list2: List<T>): List<T> {
        return list1 + list2
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> combineFlatten(items: List<Observable<List<T>>>): Observable<List<T>> {
    return Observable.combineLatest(items) { list ->
        list.map { it as List<T> }.toList().flatten()
    }
}

fun <T> combineFlatten(vararg items: Observable<List<T>>): Observable<List<T>> {
    return combineFlatten(items.toList())
}

fun <T> combineToList(items: List<Observable<T>>): Observable<List<T>> {
    return Observable.combineLatest(items) { it.map { it as T }.toList() }
}

fun <T> combineToList(vararg items: Observable<T>): Observable<List<T>> {
    return combineToList(items.toList())
}

fun <T> Observable<T>.subscribeNoError(subscribe: (T) -> Unit): Disposable {
    return subscribe(subscribe, {})
}

fun <T> Observable<List<T>>.scanConcat(): Observable<List<T>> {
    return scan(ListConcat())
}

fun Observable<Int>.scanSum(): Observable<Int> {
    return scan(IntSum())
}

class IntSum : BiFunction<Int, Int, Int> {
    override fun apply(t1: Int, t2: Int): Int {
        return t1 + t2
    }
}

//region View Bindings
fun <T : CharSequence> TextView.bind(observable: Observable<T>): Disposable {
    return observable.subscribeNoError {
        text = it
    }
}

fun TextView.bindStringResource(observable: Observable<Int>): Disposable {
    return observable.subscribeNoError {
        setText(it)
    }
}

fun ImageView.bindUrl(urlObservable: Observable<String>): Disposable {
    return urlObservable.subscribeNoError {
        Glide.with(this)
                .load(it)
                .into(this)
    }
}

fun ImageView.bindRes(resourceObservable: Observable<Int>): Disposable {
    return resourceObservable.subscribeNoError {
        imageResource = it
    }
}
//endregion

fun <T> Observable<T>.filterSuccess(): Observable<T> {
    return materialize()
            .filter { it.isOnNext || it.isOnComplete }
            .dematerialize<T>()
}

fun <T> Observable<Notification<T>>.success(): Observable<T> {
    return filter { it.isOnNext || it.isOnComplete }
            .dematerialize<T>()
}

fun <T> Observable<Notification<T>>.failure(t: T): Observable<T> {
    return filter { it.isOnError }
            .map { t }
}

fun <T> Observable<List<T>>.filterList(filter: (T) -> Boolean): Observable<List<T>> {
    return map { it.filter(filter) }
}

fun <T, R> Observable<List<T>>.mapList(map: (T) -> R): Observable<List<R>> {
    return map { it.map(map) }
}

fun <T> Observable<T>.behavior(debug: Boolean = false): Observable<T> {
    return BehaviorObservable(this, debug)
}

class BehaviorObservable<T>(val source: Observable<T>, val debug: Boolean = false) : Observable<T>(), Observer<T> {

    var cached: T? = null
    val observers = mutableListOf<Observer<in T>>()
    var sourceDisposable: Disposable? = null

    override fun subscribeActual(observer: Observer<in T>) {
        if (cached != null) {
            observer.onNext(cached!!)
        }

        observer.onSubscribe(ChildDisposable(observer))
        observers.add(observer)

        if (observers.count() == 1) {
            source.subscribe(this)
        }
    }

    private fun dispose(observer: Observer<in T>) {
        observers.remove(observer)
        if (observers.size == 0) {
            sourceDisposable?.dispose()
        }
    }

    inner class ChildDisposable(val observer: Observer<in T>) : Disposable {

        override fun isDisposed(): Boolean {
            return observers.contains(observer)
        }

        override fun dispose() {
            dispose(observer)
        }
    }

    //region Observer
    override fun onSubscribe(d: Disposable) {
        sourceDisposable = d
    }

    override fun onNext(t: T) {
        cached = t
        observers.forEach { it.onNext(t) }
    }

    override fun onComplete() {
        observers.forEach { it.onComplete() }
        observers.clear()
    }

    override fun onError(e: Throwable) {
        observers.forEach { it.onError(e) }
        observers.clear()
    }
    //endregion
}

data class MergeCombineWrapper<T>(val value: T? = null)

fun <T : Any> mergeCombine(items: List<Observable<T>>): Observable<List<T>> {
    return combineToList(
            items.map {
                it.map {
                    MergeCombineWrapper(it)
                }
                        .startWith(MergeCombineWrapper())
            }
    )
            .mapList {
                it.value
            }
            .map {
                it.filterNotNull()
            }
            .filter {
                it.isNotEmpty()
            }
}

fun <T : Any> mergeCombine(vararg items: Observable<T>): Observable<List<T>> {
    return mergeCombine(items.toList())
}

fun <T1, T2> Observable<Pair<T1, T2>>.swap(): Observable<Pair<T2, T1>> {
    return map { Pair(it.second, it.first) }
}

fun <T> Observable<Set<T>>.accumulateWith(add: Observable<T>, remove: Observable<T>): Observable<Set<T>> {
    return Observable.create { emitter ->

        var list = mutableSetOf<T>()
        val disposables = mutableListOf<Disposable>()
        var locked = false

        disposables += this.subscribe {
            if (!locked) {
                list = it.toMutableSet()
                emitter.onNext(list)
            }
        }

        disposables += add.subscribe {
            locked = true
            list.add(it)
            emitter.onNext(list)
        }

        disposables += remove.subscribe {
            locked = true
            list.remove(it)
            emitter.onNext(list)
        }

        emitter.onDispose {
            disposables.forEach { it.dispose() }
        }
    }
}

fun <T> ObservableEmitter<T>.onDispose(function: () -> Unit) {
    setDisposable(object : Disposable {
        var disposed = false
        override fun isDisposed(): Boolean {
            return disposed
        }

        override fun dispose() {
            disposed = true
            function()
        }
    })
}
