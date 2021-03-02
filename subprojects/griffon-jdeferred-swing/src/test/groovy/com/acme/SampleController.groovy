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
package com.acme

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.jdeferred.Promise
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.jdeferred2.Deferred

@ArtifactProviderFor(GriffonController)
class SampleController extends AbstractGriffonController {
    @MVCMember
    SampleModel model

    @ControllerAction
    void explicit(@Nonnull Deferred deferred) {
        model.count = model.count + 1
        1.upto(3) { deferred.notify(it) }
        if (model.count % 2 == 0) {
            throw new IllegalStateException('boom!')
        }
        deferred.resolve(model.count)
    }

    @Promise
    int implicit() {
        model.count = model.count + 1
        if (model.count % 2 == 0) {
            throw new IllegalStateException('boom!')
        }
        model.count
    }
}
