/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.jdeferred;

import griffon.core.controller.Action;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionParameter;
import griffon.plugins.jdeferred.Promise;
import griffon.plugins.jdeferred.PromiseManager;
import griffon.plugins.jdeferred.RecordingPromise;
import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.AnnotationUtils.isAnnotatedWith;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultPromiseManager implements PromiseManager {
    private static final String ERROR_ACTION_NULL = "Argument 'action' must not be null";
    private final PromiseCache promiseCache = new PromiseCache();
    private final DeferredCache deferredCache = new DeferredCache();

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <D, F, P> RecordingPromise<D, F, P> promiseFor(@Nonnull Action action) {
        requireNonNull(action, ERROR_ACTION_NULL);

        if (!isPromise(action)) {
            throw new IllegalArgumentException("Action " + action.getFullyQualifiedName() + " cannot be executed as a Promise");
        }

        RecordingPromise promise = promiseCache.get(action);
        if (promise == null) {
            promise = new DefaultRecordingPromise<>();
            promiseCache.set(action, promise);
        }
        return promise;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <D, F, P> Deferred<D, F, P> deferredFor(@Nonnull Action action) {
        requireNonNull(action, ERROR_ACTION_NULL);

        if (isPromise(action)) {
            Deferred deferred = deferredCache.get(action);
            if (deferred == null) {
                deferred = new DeferredObject<>();
                deferredCache.set(action, deferred);
            }
            return deferred;
        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <D, F, P> Deferred<D, F, P> clearDeferredFor(@Nonnull Action action) {
        requireNonNull(action, ERROR_ACTION_NULL);
        if (isPromise(action)) {
            return deferredCache.clear(action);
        }
        return null;
    }

    private boolean isPromise(@Nonnull Action action) {
        ActionMetadata actionMetadata = action.getActionMetadata();
        for (Annotation annotation : actionMetadata.getAnnotations()) {
            if (Promise.class.equals(annotation.annotationType())) {
                return true;
            }
            if (isAnnotatedWith(annotation, Promise.class, true)) {
                return true;
            }
        }

        ActionParameter[] parameters = actionMetadata.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (Deferred.class.isAssignableFrom(parameters[i].getType())) {
                return true;
            }
        }

        return false;
    }

    private static class PromiseCache {
        private final Map<WeakReference<Action>, RecordingPromise> cache = new ConcurrentHashMap<>();

        @Nullable
        public RecordingPromise get(@Nonnull Action action) {
            synchronized (cache) {
                for (Map.Entry<WeakReference<Action>, RecordingPromise> entry : cache.entrySet()) {
                    Action test = entry.getKey().get();
                    if (test == action) {
                        return entry.getValue();
                    }
                }
            }
            return null;
        }

        public void set(@Nonnull Action action, @Nonnull RecordingPromise promise) {
            WeakReference<Action> existingController = null;
            synchronized (cache) {
                for (WeakReference<Action> key : cache.keySet()) {
                    if (key.get() == action) {
                        existingController = key;
                        break;
                    }
                }
            }

            if (null != existingController) {
                cache.remove(existingController);
            }

            cache.put(new WeakReference<>(action), promise);
        }
    }

    private static class DeferredCache {
        private final Map<WeakReference<Action>, Deferred> cache = new ConcurrentHashMap<>();

        @Nullable
        public Deferred get(@Nonnull Action action) {
            synchronized (cache) {
                for (Map.Entry<WeakReference<Action>, Deferred> entry : cache.entrySet()) {
                    Action test = entry.getKey().get();
                    if (test == action) {
                        return entry.getValue();
                    }
                }
            }
            return null;
        }

        public void set(@Nonnull Action action, @Nonnull Deferred deferred) {
            WeakReference<Action> existingController = null;
            synchronized (cache) {
                for (WeakReference<Action> key : cache.keySet()) {
                    if (key.get() == action) {
                        existingController = key;
                        break;
                    }
                }
            }

            if (null != existingController) {
                cache.remove(existingController);
            }

            cache.put(new WeakReference<>(action), deferred);
        }

        @Nullable
        public Deferred clear(@Nonnull Action action) {
            WeakReference<Action> key = null;
            synchronized (cache) {
                for (Map.Entry<WeakReference<Action>, Deferred> entry : cache.entrySet()) {
                    Action test = entry.getKey().get();
                    if (test == action) {
                        key = entry.getKey();
                        break;
                    }
                }
            }

            if (key != null) {
                return cache.remove(key);
            }

            return null;
        }
    }
}
