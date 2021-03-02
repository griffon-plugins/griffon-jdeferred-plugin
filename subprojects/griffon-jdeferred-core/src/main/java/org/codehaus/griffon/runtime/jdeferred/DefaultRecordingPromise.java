/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2017-2021 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.jdeferred;

import griffon.annotations.core.Nonnull;
import griffon.plugins.jdeferred.RecordingPromise;
import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.AlwaysPipe;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.DoneFilter;
import org.jdeferred2.DonePipe;
import org.jdeferred2.FailCallback;
import org.jdeferred2.FailFilter;
import org.jdeferred2.FailPipe;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.ProgressFilter;
import org.jdeferred2.ProgressPipe;
import org.jdeferred2.Promise;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultRecordingPromise<D, F, P> implements RecordingPromise<D, F, P> {
    private static final String ERROR_ALWAYS_PIPE_NULL = "Argument 'alwaysPipe' must not be null";
    private static final String ERROR_DONE_PIPE_NULL = "Argument 'donePipe' must not be null";
    private static final String ERROR_DONE_FILTER_NULL = "Argument 'doneFilter' must not be null";
    private static final String ERROR_DONE_CALLBACK_NULL = "Argument 'doneCallback' must not be null";
    private static final String ERROR_FAIL_PIPE_NULL = "Argument 'falPipe' must not be null";
    private static final String ERROR_FAIL_FILTER_NULL = "Argument 'falFilter' must not be null";
    private static final String ERROR_FAIL_CALLBACK_NULL = "Argument 'failCallback' must not be null";
    private static final String ERROR_ALWAYS_CALLBACK_NULL = "Argument 'alwaysCallback' must not be null";
    private static final String ERROR_PROGRESS_PIPE_NULL = "Argument 'progressPipe' must not be null";
    private static final String ERROR_PROGRESS_FILTER_NULL = "Argument 'progressFilter' must not be null";
    private static final String ERROR_PROGRESS_CALLBACK_NULL = "Argument 'progressCallback' must not be null";
    private static final String ERROR_PROMISE_NULL = "Argument 'promise' must not be null";

    private final List<Function<Promise, Promise>> functions = new ArrayList<>();

    public DefaultRecordingPromise() {

    }

    private DefaultRecordingPromise(@Nonnull List<Function<Promise, Promise>> functions) {
        this.functions.addAll(functions);
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback) {
        return done(doneCallback);
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback, @Nonnull FailCallback<F> failCallback) {
        done(doneCallback);
        return fail(failCallback);
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback, @Nonnull FailCallback<F> failCallback, @Nonnull ProgressCallback<P> progressCallback) {
        done(doneCallback);
        fail(failCallback);
        return progress(progressCallback);
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> done(@Nonnull final DoneCallback<D> doneCallback) {
        requireNonNull(doneCallback, ERROR_DONE_CALLBACK_NULL);
        functions.add(p -> p.done(doneCallback));
        return this;
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> fail(@Nonnull final FailCallback<F> failCallback) {
        requireNonNull(failCallback, ERROR_FAIL_CALLBACK_NULL);
        functions.add(p -> p.fail(failCallback));
        return this;
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> always(@Nonnull final AlwaysCallback<D, F> alwaysCallback) {
        requireNonNull(alwaysCallback, ERROR_ALWAYS_CALLBACK_NULL);
        functions.add(p -> p.always(alwaysCallback));
        return this;
    }

    @Nonnull
    @Override
    public RecordingPromise<D, F, P> progress(@Nonnull final ProgressCallback<P> progressCallback) {
        requireNonNull(progressCallback, ERROR_PROGRESS_CALLBACK_NULL);
        functions.add(p -> p.progress(progressCallback));
        return this;
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull final DoneFilter<D, D_OUT> doneFilter) {
        requireNonNull(doneFilter, ERROR_DONE_FILTER_NULL);
        functions.add(p -> p.filter(doneFilter));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull final DoneFilter<D, D_OUT> doneFilter, @Nonnull final FailFilter<F, F_OUT> failFilter) {
        requireNonNull(doneFilter, ERROR_DONE_FILTER_NULL);
        requireNonNull(failFilter, ERROR_FAIL_FILTER_NULL);
        functions.add(p -> p.filter(doneFilter, failFilter));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull final DoneFilter<D, D_OUT> doneFilter, @Nonnull final FailFilter<F, F_OUT> failFilter, @Nonnull final ProgressFilter<P, P_OUT> progressFilter) {
        requireNonNull(doneFilter, ERROR_DONE_FILTER_NULL);
        requireNonNull(failFilter, ERROR_FAIL_FILTER_NULL);
        requireNonNull(progressFilter, ERROR_PROGRESS_FILTER_NULL);
        functions.add(p -> p.filter(doneFilter, failFilter, progressFilter));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull final DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
        requireNonNull(donePipe, ERROR_DONE_PIPE_NULL);
        functions.add(p -> p.pipe(donePipe));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull final DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull final FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
        requireNonNull(donePipe, ERROR_DONE_PIPE_NULL);
        requireNonNull(failPipe, ERROR_FAIL_PIPE_NULL);
        functions.add(p -> p.pipe(donePipe, failPipe));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull final DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull final FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, @Nonnull final ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        requireNonNull(donePipe, ERROR_DONE_PIPE_NULL);
        requireNonNull(failPipe, ERROR_FAIL_PIPE_NULL);
        requireNonNull(progressPipe, ERROR_PROGRESS_PIPE_NULL);
        functions.add(p -> p.pipe(donePipe, failPipe, progressPipe));
        return new DefaultRecordingPromise<>(functions);
    }

    @Override
    public <D_OUT, F_OUT> RecordingPromise<D_OUT, F_OUT, P> pipeAlways(AlwaysPipe<? super D, ? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> alwaysPipe) {
        requireNonNull(alwaysPipe, ERROR_ALWAYS_PIPE_NULL);
        functions.add(p -> p.pipeAlways(alwaysPipe));
        return new DefaultRecordingPromise<>(functions);
    }

    @Nonnull
    @Override
    public Promise<D, F, P> applyTo(@Nonnull Promise<D, F, P> promise) {
        requireNonNull(promise, ERROR_PROMISE_NULL);
        Promise<D, F, P> p = promise;

        for (Function<Promise, Promise> f : functions) {
            p = f.apply(p);
        }

        return p;
    }

    private interface Function<T, R> {
        R apply(T t);
    }
}
