/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.json.viewer;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.isis.viewer.json.applib.RestfulMediaType;
import org.apache.isis.viewer.json.applib.RestfulResponse.HttpStatusCode;
import org.apache.isis.viewer.json.applib.util.JsonMapper;

import com.google.common.collect.Lists;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        ResponseBuilder builder = 
                Response.status(HttpStatusCode.INTERNAL_SERVER_ERROR.getJaxrsStatusType())
                .type(RestfulMediaType.APPLICATION_JSON_ERROR)
                .entity(jsonFor(ex));
        return builder.build();
    }


    private static class ExceptionPojo {

        public static ExceptionPojo create(Exception ex) {
            return new ExceptionPojo(ex);
        }

        private static String format(StackTraceElement stackTraceElement) {
            return stackTraceElement.toString();
        }

        private final String message;
        private final List<String> stackTrace = Lists.newArrayList();
        private ExceptionPojo causedBy;

        public ExceptionPojo(Throwable ex) {
            this.message = ex.getMessage();
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                this.stackTrace.add(format(stackTraceElement));
            }
            Throwable cause = ex.getCause();
            if(cause != null && cause != ex) {
                this.causedBy = new ExceptionPojo(cause);
            }
        }

        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
        
        @SuppressWarnings("unused")
        public List<String> getStackTrace() {
            return stackTrace;
        }
        
        @SuppressWarnings("unused")
        public ExceptionPojo getCausedBy() {
            return causedBy;
        }
    }
    
    static String jsonFor(Exception ex) {
        try {
            return JsonMapper.instance().write(ExceptionPojo.create(ex));
        } catch (Exception e) {
            // fallback
            return "{ \"exception\": \"" + ExceptionUtils.getFullStackTrace(ex) + "\" }";
        }
    }
}