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
import griffon.core.controller.Action;
import griffon.core.controller.ActionExecutionStatus;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionParameter;
import griffon.exceptions.InstanceMethodInvocationException;
import griffon.plugins.jdeferred.PromiseManager;
import griffon.plugins.jdeferred.RecordingPromise;
import org.codehaus.griffon.runtime.core.controller.AbstractActionHandler;
import org.jdeferred2.Deferred;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Andres Almiray
 */
@Named("jdeferred")
public class JDeferredActionHandler extends AbstractActionHandler {
    @Inject
    private PromiseManager promiseManager;

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public Object[] before(@Nonnull Action action, @Nonnull Object[] args) {
        ActionMetadata actionMetadata = action.getActionMetadata();

        Deferred deferred = promiseManager.deferredFor(action);
        if (deferred != null) {
            RecordingPromise promise = promiseManager.promiseFor(action);
            deferred = (Deferred) promise.applyTo(deferred);
        }

        ActionParameter[] parameters = actionMetadata.getParameters();
        Object[] newArgs = new Object[parameters.length];
        for (int i = 0; i < newArgs.length; i++) {
            if (Deferred.class.isAssignableFrom(parameters[i].getType())) {
                newArgs[i] = deferred;
            } else {
                newArgs[i] = args.length > i ? args[i] : null;
            }
        }
        return newArgs;
    }

    @Override
    public Object after(@Nonnull ActionExecutionStatus status, @Nonnull Action action, @Nonnull Object[] args, @Nullable Object result) {
        result = super.after(status, action, args, result);

        Deferred deferred = promiseManager.clearDeferredFor(action);
        if (deferred != null && deferred.isPending()) {
            deferred.resolve(result);
            return deferred;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean exception(@Nonnull Exception exception, @Nonnull Action action, @Nonnull Object[] args) {
        Deferred deferred = promiseManager.clearDeferredFor(action);
        if (deferred != null && deferred.isPending()) {
            Throwable t = exception;
            if (t instanceof InvocationTargetException || t instanceof InstanceMethodInvocationException) {
                t = t.getCause();
            }
            deferred.reject(t);
            return true;
        }
        return super.exception(exception, action, args);
    }
}
