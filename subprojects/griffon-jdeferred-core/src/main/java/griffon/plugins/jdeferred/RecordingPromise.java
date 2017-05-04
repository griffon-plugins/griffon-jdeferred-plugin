/*
 * Copyright 2017 the original author or authors.
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
package griffon.plugins.jdeferred;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public interface RecordingPromise<D, F, P> {
    @Nonnull
    RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback);

    @Nonnull
    RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback, @Nonnull FailCallback<F> failCallback);

    @Nonnull
    RecordingPromise<D, F, P> then(@Nonnull DoneCallback<D> doneCallback, @Nonnull FailCallback<F> failCallback, @Nonnull ProgressCallback<P> progressCallback);

    @Nonnull
    RecordingPromise<D, F, P> done(@Nonnull DoneCallback<D> doneCallback);

    @Nonnull
    RecordingPromise<D, F, P> fail(@Nonnull FailCallback<F> failCallback);

    @Nonnull
    RecordingPromise<D, F, P> always(@Nonnull AlwaysCallback<D, F> alwaysCallback);

    @Nonnull
    RecordingPromise<D, F, P> progress(@Nonnull ProgressCallback<P> progressCallback);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DoneFilter<D, D_OUT> doneFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DoneFilter<D, D_OUT> doneFilter, @Nonnull FailFilter<F, F_OUT> failFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DoneFilter<D, D_OUT> doneFilter, @Nonnull FailFilter<F, F_OUT> failFilter, @Nonnull ProgressFilter<P, P_OUT> progressFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> then(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, @Nonnull ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe);

    @Nonnull
    Promise<D, F, P> applyTo(@Nonnull Promise<D, F, P> promise);
}
