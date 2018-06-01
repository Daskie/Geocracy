package csc309.geocracy;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


public final class EventBus {

    private static Map<String, PublishSubject<Object>> subjectMap = new HashMap<>();
    private static Map<Object, CompositeDisposable> subscriptionsMap = new HashMap<>();

    private EventBus() {}

    /**
     * Get the subject or create it if it's not already in memory.
     */
    @NonNull
    private static PublishSubject<Object> getSubject(String subjectKey) {
        PublishSubject<Object> subject = subjectMap.get(subjectKey);

        if (subject == null) {

            subject = PublishSubject.create();
            subjectMap.put(subjectKey, subject);

        }

        return subject;
    }

    /**
     * Get the CompositeDisposable or create it if it's not already in memory.
     */
    @NonNull
    private static CompositeDisposable getCompositeDisposable(@NonNull Object object) {
        CompositeDisposable compositeDisposable = subscriptionsMap.get(object);
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            subscriptionsMap.put(object, compositeDisposable);
        }

        return compositeDisposable;
    }

    /**
     * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
     * your registration with, so that you can unsubscribe later.
     * <br/><br/>
     * <b>Note:</b> Make sure to call {@link EventBus#unregister(Object)} to avoid memory leaks.
     */
    public static void subscribe(String subject, @NonNull Object lifecycle, @NonNull Consumer<Object> action) {
        Disposable disposable = getSubject(subject).subscribe(action);
        getCompositeDisposable(lifecycle).add(disposable);
    }

    /**
     * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
     * your registration with, so that you can unsubscribe later.
     * <br/><br/>
     * <b>Note:</b> Make sure to call {@link EventBus#unregister(Object)} to avoid memory leaks.
     */
    public static PublishSubject<Object> subscribe(String subject, @NonNull Object lifecycle) {
        return getSubject(subject);
    }

    /**
     * Unregisters this object from the bus, removing all subscriptions.
     * This should be called when the object is going to go out of memory.
     */
    public static void unregister(@NonNull Object lifecycle) {
        //We have to remove the composition from the map, because once you dispose it can't be used anymore
        CompositeDisposable compositeDisposable = subscriptionsMap.remove(lifecycle);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    /**
     * Publish an object to the specified subject for all subscribers of that subject.
     */
    public static void publish(String subject, @NonNull Object message) {
        getSubject(subject).onNext(message);
    }
}