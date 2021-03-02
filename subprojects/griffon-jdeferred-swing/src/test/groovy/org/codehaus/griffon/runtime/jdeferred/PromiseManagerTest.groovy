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
package org.codehaus.griffon.runtime.jdeferred

import com.acme.SampleController
import com.acme.SampleModel
import griffon.core.GriffonApplication
import griffon.core.artifact.ArtifactManager
import griffon.core.controller.ActionManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.plugins.jdeferred.PromiseManager
import org.jdeferred2.Promise.State
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static org.hamcrest.Matchers.equalTo

@TestFor(SampleController)
class PromiseManagerTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')
        System.setProperty('griffon.full.stacktrace', 'true')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private PromiseManager promiseManager
    @Inject
    private ArtifactManager artifactManager
    @Inject
    private ActionManager actionManager
    @Inject
    private GriffonApplication application

    private SampleController controller

    @Test
    void invokeExplicitPromise() {
        // given:
        application.startup()

        controller.model = artifactManager.newInstance(SampleModel)
        List<Integer> done = []
        List<Throwable> fail = []
        List<State> always = []
        promiseManager.promiseFor(actionManager.actionsFor(controller).explicit)
            .done({ v -> done << v })
            .fail({ t -> fail << t })
            .always({ s, v, t -> always << s })

        // when:
        controller.invokeAction('explicit')
        await().atMost(2, SECONDS)
            .until({ controller.model.count }, equalTo(1))

        // then:
        assert done[0] == 1
        assert !fail[0]
        assert always[0] == State.RESOLVED

        // when:
        controller.invokeAction('explicit')
        await().atMost(2, SECONDS)
            .until({ controller.model.count }, equalTo(2))

        // then:
        assert !done[1]
        assert fail[0] instanceof IllegalStateException
        assert always[1] == State.REJECTED
    }

    @Test
    void invokeImplicitPromise() {
        // given:
        application.startup()

        controller.model = artifactManager.newInstance(SampleModel)
        List<Integer> done = []
        List<Throwable> fail = []
        List<State> always = []
        promiseManager.promiseFor(actionManager.actionsFor(controller).implicit)
            .done({ v -> done << v })
            .fail({ t -> fail << t })
            .always({ s, v, t -> always << s })

        // when:
        controller.invokeAction('implicit')
        await().atMost(2, SECONDS)
            .until({ controller.model.count }, equalTo(1))

        // then:
        assert done[0] == 1
        assert !fail[0]
        assert always[0] == State.RESOLVED

        // when:
        controller.invokeAction('implicit')
        await().atMost(2, SECONDS)
            .until({ controller.model.count }, equalTo(2))

        // then:
        assert !done[1]
        assert fail[0] instanceof IllegalStateException
        assert always[1] == State.REJECTED
    }
}
