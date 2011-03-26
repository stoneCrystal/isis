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


package org.apache.isis.viewer.scimpi.dispatcher.view.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.isis.applib.filter.Filter;
import org.apache.isis.core.commons.debug.DebugBuilder;
import org.apache.isis.core.commons.debug.DebugHtmlString;
import org.apache.isis.core.commons.debug.DebugString;
import org.apache.isis.core.commons.debug.DebuggableWithTitle;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.spec.ActionType;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoader;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.ObjectMember;
import org.apache.isis.runtimes.dflt.runtime.context.IsisContext;
import org.apache.isis.core.metamodel.util.Dump;
import org.apache.isis.viewer.scimpi.dispatcher.AbstractElementProcessor;
import org.apache.isis.viewer.scimpi.dispatcher.Dispatcher;
import org.apache.isis.viewer.scimpi.dispatcher.context.RequestContext;
import org.apache.isis.viewer.scimpi.dispatcher.processor.Request;


public class Debug extends AbstractElementProcessor {

    private final Dispatcher dispatcher;

    public Debug(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;}
    
    @Override
    public void process(Request request) {
        if (request.getContext().isDebugDisabled()) {
            return;
        }
          
        //  Application  | System  | Specifications  | Dispatcher  | Context  | Variables  | Object  | I18N File  | Authorization File  | Hide Debug  
        
        String type = request.getOptionalProperty(TYPE);
       
        boolean alwaysShow = request.isRequested("force", false);
         if (type != null) {
            if (type.equals("system")) {
                displaySystem(request);
            } else if (type.equals("variables")) {
                displayVariables(request);
            } else if (type.equals("dispatcher")) {
                displayDispatcher(request);
            } else if (type.equals("context")) {
                displayContext(request);
             } else if (type.equals("specifications")) {
                listSpecifications(request);
             } else if (type.equals("specification-for")) {
                 specificationFor(request);
             } else if (type.equals("specification")) {
                 specification(request);

                
                
            } else if (type.equals("object")) {
                String value = request.getOptionalProperty(VALUE);
                RequestContext context = request.getContext();
                ObjectAdapter object = context.getMappedObject(value);
                DebugString str = new DebugString();
                Dump.adapter(object, str);
                Dump.graph(object, str, IsisContext.getAuthenticationSession());
                request.appendHtml("<h2>" + object.getSpecification().getFullIdentifier() + "</h2>");
                request.appendHtml("<pre class=\"debug\">" + str + "</pre>");
            }

        }

        if (alwaysShow || request.getContext().getDebug() == RequestContext.Debug.ON) {

            RequestContext context = request.getContext();
            
            
            

            String id = request.getOptionalProperty("object");
            if (id != null) {
                ObjectAdapter object = context.getMappedObject(id);
                if (object instanceof DebuggableWithTitle) {
                    DebugString debug = new DebugString();
                    ((DebuggableWithTitle) object).debugData(debug);
                    request.appendHtml("<pre class=\"debug\">" + debug + "</pre>");
                } else {
                    request.appendHtml(object.toString());
                }
            }

            String variable = request.getOptionalProperty("variable");
            if (variable != null) {
                Object object = context.getVariable(variable);
                request.appendHtml(variable + " => " + (object == null ? "null" : object.toString()));
            }

            String list = request.getOptionalProperty("list");
            if (list != null) {
                DebugString debug = new DebugString();
                context.append(debug, list);
                request.appendHtml(debug.toString());
            }

            String uri = request.getOptionalProperty("uri");
            if (uri != null) {
                request.appendHtml("<pre class=\"debug\">");
                request.appendHtml(context.getUri());
                request.appendHtml("</pre>");
            }

        }
    }
    
    protected void specificationFor(Request request) {
        String id = request.getOptionalProperty(VALUE);
        ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
        specification(request, object.getSpecification());
    }
    
    protected void specification(Request request) {
        String name = request.getOptionalProperty(VALUE);
        ObjectSpecification spec = getSpecificationLoader().loadSpecification(name);
        specification(request, spec);
    }

    private void specification(Request request, ObjectSpecification spec) {
        request.appendHtml("<h1>Specification - " + spec.getFullIdentifier() + "</h1>");
        DebugBuilder debug = new DebugHtmlString();
        specification(spec, debug);
        request.appendHtml(debug.toString());
    }

    private void displayContext(Request request) {
        request.appendHtml("<h1>Context</h1>");
        DebugHtmlString debugString = new DebugHtmlString();
        request.getContext().append(debugString);
        request.appendHtml(debugString.toString());
    }

    private void displayDispatcher(Request request) {
        request.appendHtml("<h1>Dispatcher</h1>");
        DebugHtmlString debugString = new DebugHtmlString();
        dispatcher.debug(debugString);
        request.appendHtml(debugString.toString());
    }

    protected void displayVariables(Request request) {
        request.appendHtml("<h1>Variables</h1>");
        DebugHtmlString debug = new DebugHtmlString();
        RequestContext context = request.getContext();
        context.append(debug, "variables");
        request.appendHtml(debug.toString());
    }

    protected void displaySystem(Request request) {
        request.appendHtml("<h1>System</h1>");
        DebuggableWithTitle[] debug = IsisContext.debugSystem();
        for (int i = 0; i < debug.length; i++) {
            DebugHtmlString str = new DebugHtmlString();
            str.appendTitle(debug[i].debugTitle());
            debug[i].debugData(str);
            request.appendHtml(str.toString());
        }
    }

    protected void listSpecifications(Request request) {
        request.appendHtml("<h1>Specifications</h1>");
        List<ObjectSpecification> fullIdentifierList = new ArrayList<ObjectSpecification>(getSpecificationLoader().allSpecifications());
        Collections.sort(fullIdentifierList, ObjectSpecification.COMPARATOR_SHORT_IDENTIFIER_IGNORE_CASE);
        DebugHtmlString debug = new DebugHtmlString();
        for (ObjectSpecification spec : fullIdentifierList) {
            String name = spec.getSingularName();
            debug.appendln(name, specificationLink(spec));
        }
        request.appendHtml(debug.toString());
    }

    private String specificationLink(ObjectSpecification specification) {
        if (specification == null) {
            return "none";
        } else {
            String name = specification.getFullIdentifier();
            return "<a href=\"./debug.shtml?type=specification&value=" + name + "\">" + name + "</a>";
        }
    }
    
    protected SpecificationLoader getSpecificationLoader() {
        return IsisContext.getSpecificationLoader();
    }

    @Override
    public String getName() {
        return "debug";
    }

    private void specification(ObjectSpecification spec, DebugBuilder view) {
        view.startSection("Summary");
        view.appendln("Hash code", "#" + Integer.toHexString(spec.hashCode()));
        view.appendln("ID", spec.getIdentifier());
        view.appendln("Full name", spec.getFullIdentifier());
        view.appendln("Short name", spec.getShortIdentifier());
        view.appendln("Singular name", spec.getSingularName());
        view.appendln("Plural name", spec.getPluralName());
        view.appendln("Description", spec.getDescription());
    
        view.appendln("Type", "?");
        view.appendln("Value/aggregated", String.valueOf(!spec.isValueOrIsAggregated()));
    
        view.appendln("Parent specification", specificationLink(spec.superclass()));
        specificationClasses(view, "Child specifications",  spec.subclasses());
        specificationClasses(view, "Implemented interfaces", spec.interfaces());
        speficationFacets(view, spec);
        
        List<ObjectAssociation> fields = spec.getAssociations();
        specificationMembers(view, "Fields", fields);
        List<ObjectAction> userActions = spec.getObjectActions(ActionType.USER);
        specificationMembers(view, "User Actions", userActions);
        specificationMembers(view, "Exploration Actions", spec.getObjectActions(ActionType.EXPLORATION));
        specificationMembers(view, "Prototype Actions", spec.getObjectActions(ActionType.PROTOTYPE));
        specificationMembers(view, "Debug Actions", spec.getObjectActions(ActionType.DEBUG));
        view.endSection();
        
        view.startSection("Fields");
        for (int i = 0; i < fields.size(); i++) {
            ObjectAssociation field = fields.get(i);
            view.appendTitle("<span id=\"" + field.getId() + "\"><em>Field:</em> " + field.getId() + "</span>");
            view.appendln("ID", field.getIdentifier());
            view.appendln("Short ID", field.getId());
            view.appendln("Name", field.getName());
            view.appendln("Specification", specificationLink(field.getSpecification()));
    
            view.appendln("Type",  field.isOneToManyAssociation() ? "Collection" : field.isOneToOneAssociation() ? "Object" : "Unknown");
            view.appendln("Flags", (field.isAlwaysHidden() ? "": "Visible ") + (field.isNotPersisted() ? "Not-Persisted ": " ")
                    + (field.isMandatory() ? "Mandatory " : ""));
    
            speficationFacets(view, field);
        }
        view.endSection();
        
        view.startSection("Actions");
        for (int i = 0; i < userActions.size(); i++) {
            final ObjectAction action = userActions.get(i);
            view.appendTitle("<span id=\"" + action.getId() + "\"><em>Action:</em> " + action.getId() + "</span>");
            view.appendln("ID", action.getIdentifier());
            view.appendln("Short ID", action.getId());
            view.appendln("Name", action.getName());
            view.appendln("Specification", specificationLink(action.getSpecification()));
    
            view.appendln("Target", action.getTarget());
            view.appendln("On type", specificationLink(action.getOnType()));
    
            ObjectSpecification returnType = action.getReturnType();
            view.appendln("Returns", returnType == null ? "VOID" : specificationLink(returnType));
            
            speficationFacets(view, action);
    
            List<ObjectActionParameter> parameters = action.getParameters();
            if (parameters.size() == 0) {
                view.appendln("Parameters", "none");
            } else {
                StringBuffer buffer = new StringBuffer();
                List<ObjectActionParameter> p = action.getParameters();
                for (int j = 0; j < parameters.size(); j++) {
                    buffer.append(p.get(j).getName());
                    buffer.append(" (");
                    buffer.append(specificationLink(parameters.get(j).getSpecification()));
                    buffer.append(")");
                    view.appendln("Parameters", buffer.toString());
                    
                    view.indent();
                    Class<? extends Facet>[] parameterFacets = p.get(j).getFacetTypes();
                    for (int k = 0; k < parameterFacets.length; k++) {
                        view.append(p.get(j).getFacet(parameterFacets[k]).toString());
                    }
                    view.unindent();
                }
            }
        }
    }

    private void specificationClasses(DebugBuilder view, String label, List<ObjectSpecification> subclasses) {
        StringBuffer buffer = new StringBuffer();
        if (subclasses.size() == 0) {
            buffer.append("none");
        } else {
            for (int i = 0; i < subclasses.size(); i++) {
                buffer.append(specificationLink(subclasses.get(i)) + "<br>");
            }
        }
        view.appendln(label, buffer.toString());
    }

    private void specificationMembers(DebugBuilder view, String label, List<? extends ObjectMember> members) {
        StringBuffer buffer = new StringBuffer();
        if (members.size() == 0) {
            buffer.append("none");
        } else {
            for (int i = 0; i < members.size(); i++) {
                buffer.append("<a href=\"#" + members.get(i).getId() + "\">" + members.get(i).getId() + "</a><br>");
            }
        }
        view.appendln(label, buffer.toString());
    }

    private void speficationFacets(DebugBuilder view, FacetHolder facetHolder) {
        Facet[] facets = facetHolder.getFacets(new Filter<Facet>() {
            @Override
            public boolean accept(Facet facet) {
                return true;
            }
        });
        StringBuffer buffer = new StringBuffer();
        if (facets == null || facets.length == 0) {
            buffer.append("none");
        } else {
            Arrays.sort(facets, new Comparator<Facet>() {
                public int compare(Facet o1, Facet o2) {
                    String facetType1 = o1.facetType().getName();
                    String facetType2 = o2.facetType().getName();
                    return facetType1.substring(facetType1.lastIndexOf('.') + 1).compareTo(facetType2.substring(facetType2.lastIndexOf('.') + 1));
                }});
            for (int i = 0; i < facets.length; i++) {
                String facetType = facets[i].facetType().getName();
                buffer.append("<span class=\"facet-type\">" + facetType .substring(facetType.lastIndexOf('.') + 1)  + "</span>:  " + facets[i] + "<br>");
            }
        }
        view.appendln("Facets", buffer.toString());
    }

}

