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
package org.apache.isis.viewer.restfulobjects.tck.domainobjectorservice.id.action.invoke;

import java.io.IOException;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.LinkRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RestfulHttpMethod;
import org.apache.isis.viewer.restfulobjects.applib.RestfulMediaType;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulClient;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse.Header;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.ActionResultRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.ActionResultRepresentation.ResultType;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainServiceResource;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.ListRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.ObjectActionRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.util.UrlEncodingUtils;
import org.apache.isis.viewer.restfulobjects.tck.IsisWebServerRule;
import org.apache.isis.viewer.restfulobjects.tck.RestfulMatchers;
import org.apache.isis.viewer.restfulobjects.tck.Util;

import static org.apache.isis.viewer.restfulobjects.tck.RestfulMatchers.hasMediaTypeProfile;
import static org.apache.isis.viewer.restfulobjects.tck.RestfulMatchers.isLink;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Get_givenOptionalArg_whenNoArgProvided_ok {

    @Rule
    public IsisWebServerRule webServerRule = new IsisWebServerRule();

    private RestfulClient client;

    private DomainServiceResource serviceResource;

    @Before
    public void setUp() throws Exception {
        client = webServerRule.getClient();

        serviceResource = client.getDomainServiceResource();
    }

    @Test
    public void usingClientFollow_whenExplicitlySetToNull() throws Exception {

        // given
        final JsonRepresentation givenAction = Util.givenAction(client, "ActionsEntities", "subListWithOptionalRange");
        final ObjectActionRepresentation actionRepr = givenAction.as(ObjectActionRepresentation.class);

        final LinkRepresentation invokeLink = actionRepr.getInvoke();

        assertThat(invokeLink, isLink(client)
                                    .rel(Rel.INVOKE)
                                    .httpMethod(RestfulHttpMethod.GET)
                                    .href(Matchers.endsWith(":39393/services/ActionsEntities/actions/subListWithOptionalRange/invoke"))
                                    .build());
        
        JsonRepresentation args =invokeLink.getArguments();
        assertThat(args.size(), is(2));
        assertThat(args, RestfulMatchers.mapHas("from"));
        assertThat(args, RestfulMatchers.mapHas("to"));
        
        // when
        args.mapPut("from.value", (Integer)null);
        args.mapPut("to.value", (Integer)null);

        final RestfulResponse<ActionResultRepresentation> restfulResponse = client.followT(invokeLink, args);
        
        then(restfulResponse);
    }


    @Test
    public void usingResourceProxy_whenExplicitlySetToNull() throws Exception {

        // given, when
        JsonRepresentation args = JsonRepresentation.newMap();
        args.mapPut("from.value", (Integer)null);
        args.mapPut("to.value", (Integer)null);
        Response response = serviceResource.invokeActionQueryOnly("ActionsEntities", "subListWithOptionalRange", UrlEncodingUtils.urlEncode(args));
        RestfulResponse<ActionResultRepresentation> restfulResponse = RestfulResponse.ofT(response);
        
        // then
        then(restfulResponse);
    }

    
    @Test
    public void usingClientFollow_whenImplicitlySetToNull() throws Exception {

        // given
        final JsonRepresentation givenAction = Util.givenAction(client, "ActionsEntities", "subListWithOptionalRange");
        final ObjectActionRepresentation actionRepr = givenAction.as(ObjectActionRepresentation.class);

        final LinkRepresentation invokeLink = actionRepr.getInvoke();

        assertThat(invokeLink, isLink(client)
                                    .rel(Rel.INVOKE)
                                    .httpMethod(RestfulHttpMethod.GET)
                                    .href(Matchers.endsWith(":39393/services/ActionsEntities/actions/subListWithOptionalRange/invoke"))
                                    .build());
        
        // when
        JsonRepresentation args = JsonRepresentation.newMap();

        final RestfulResponse<ActionResultRepresentation> restfulResponse = client.followT(invokeLink, args);
        
        then(restfulResponse);
    }

    
    @Test
    public void usingResourceProxy_whenImplicitlySetToNull() throws Exception {

        // given, when
        JsonRepresentation args = JsonRepresentation.newMap();
        Response response = serviceResource.invokeActionQueryOnly("ActionsEntities", "subListWithOptionalRange", UrlEncodingUtils.urlEncode(args));
        RestfulResponse<ActionResultRepresentation> restfulResponse = RestfulResponse.ofT(response);
        
        // then
        then(restfulResponse);
    }

    
    private static void then(RestfulResponse<ActionResultRepresentation> restfulResponse) throws IOException {
        assertThat(restfulResponse.getHeader(Header.CONTENT_TYPE), hasMediaTypeProfile(RestfulMediaType.APPLICATION_JSON_ACTION_RESULT));
        final ActionResultRepresentation actionResultRepr = restfulResponse.getEntity();
        
        assertThat(actionResultRepr.getResultType(), is(ResultType.LIST));
        
        final ListRepresentation listRepr = actionResultRepr.getResult().as(ListRepresentation.class);

        assertThat(listRepr.getValue().size(), is(5));
    }

}
