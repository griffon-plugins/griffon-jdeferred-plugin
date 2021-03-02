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
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonController
import griffon.plugins.jdeferred.Promise

import org.jdeferred2.Deferred
import org.kordamp.jipsy.annotations.ServiceProviderFor

import javax.application.action.ActionHandler

@ServiceProviderFor(GriffonController)
class SampleController {
    @MVCMember
    SampleModel model

    @ActionHandler
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
