/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zagros.personalnotes.core;

/**
 * Represents a data query, which itself is carried out using the content provider
 * {@link com.zagros.core.ScheduleProvider}.
 */
public interface QueryEnum {

    /**
     * @return the id of the query, also used an identifier for the corresponding
     * {@link android.app.LoaderManager}
     */
    int getId();

    /**
     * @return the projection for the query. The fields in the projection are defined in
     * the {@link com.google.samples.apps.iosched.provider.ScheduleContract}
     */
    String[] getProjection();

}