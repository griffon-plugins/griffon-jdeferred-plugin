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
package griffon.plugins.jdeferred;

import griffon.annotations.core.Nonnull;
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
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull DoneFilter<D, D_OUT> doneFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull DoneFilter<D, D_OUT> doneFilter, @Nonnull FailFilter<F, F_OUT> failFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> filter(@Nonnull DoneFilter<D, D_OUT> doneFilter, @Nonnull FailFilter<F, F_OUT> failFilter, @Nonnull ProgressFilter<P, P_OUT> progressFilter);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe);

    @Nonnull
    <D_OUT, F_OUT, P_OUT> RecordingPromise<D_OUT, F_OUT, P_OUT> pipe(@Nonnull DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, @Nonnull FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, @Nonnull ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe);

    @Nonnull
    <D_OUT, F_OUT> RecordingPromise<D_OUT, F_OUT, P> pipeAlways(@Nonnull AlwaysPipe<? super D, ? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> alwaysPipe);

    @Nonnull
    Promise<D, F, P> applyTo(@Nonnull Promise<D, F, P> promise);
}
