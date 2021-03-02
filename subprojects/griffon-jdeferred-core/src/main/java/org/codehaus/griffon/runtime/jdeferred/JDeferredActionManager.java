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
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionMetadataFactory;
import griffon.exceptions.InstanceMethodInvocationException;
import griffon.plugins.jdeferred.Promise;
import griffon.plugins.jdeferred.PromiseManager;
import org.codehaus.griffon.runtime.core.controller.DefaultActionManager;
import org.jdeferred2.Deferred;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

import static griffon.util.AnnotationUtils.isAnnotatedWith;

/**
 * @author Andres Almiray
 */
public class JDeferredActionManager extends DefaultActionManager {
    private final PromiseManager promiseManager;

    @Inject
    public JDeferredActionManager(@Nonnull GriffonApplication application,
                                  @Nonnull ActionFactory actionFactory,
                                  @Nonnull ActionMetadataFactory actionMetadataFactory,
                                  @Nonnull PromiseManager promiseManager) {
        super(application, actionFactory, actionMetadataFactory);
        this.promiseManager = promiseManager;
    }

    @Nullable
    @Override
    protected Object doInvokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] updatedArgs) {
        Action action = actionFor(controller, actionName);
        if (isPromise(action)) {
            Deferred deferred = promiseManager.deferredFor(action);
            try {
                deferred.resolve(super.doInvokeAction(controller, actionName, updatedArgs));
            } catch (InstanceMethodInvocationException imie) {
                deferred.reject(imie.getCause());
            }
            return deferred;
        } else {
            return super.doInvokeAction(controller, actionName, updatedArgs);
        }
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

        return false;
    }
}
