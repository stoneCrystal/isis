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

package org.apache.isis.core.metamodel.facets.collections.javautilcollection;

import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManagerAware;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacet;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacetDefaultToObject;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacetInferredFromArray;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacetInferredFromGenerics;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;

public class CollectionFacetFactory extends FacetFactoryAbstract implements AdapterManagerAware {

    private final CollectionTypeRegistry collectionTypeRegistry = new CollectionTypeRegistry();
    private AdapterManager adapterManager;

    public CollectionFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    @Override
    public void process(final ProcessClassContext processClassContaxt) {

        if (collectionTypeRegistry.isCollectionType(processClassContaxt.getCls())) {
            processCollectionType(processClassContaxt);
        } else if (collectionTypeRegistry.isArrayType(processClassContaxt.getCls())) {
            processAsArrayType(processClassContaxt);
        }

    }

    private void processCollectionType(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder facetHolder = processClassContext.getFacetHolder();

        TypeOfFacet typeOfFacet = facetHolder.getFacet(TypeOfFacet.class);
        if (typeOfFacet == null) {
            final Class<?> collectionElementType = collectionElementType(cls);
            typeOfFacet =
                    collectionElementType != Object.class
                            ? new TypeOfFacetInferredFromGenerics(collectionElementType, facetHolder, getSpecificationLoader())
                            : new TypeOfFacetDefaultToObject(facetHolder, getSpecificationLoader());
            facetHolder.addFacet(typeOfFacet);
        }

        final CollectionFacet collectionFacet = new JavaCollectionFacet(facetHolder, getAdapterManager());

        facetHolder.addFacet(collectionFacet);
    }

    private void processAsArrayType(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder facetHolder = processClassContext.getFacetHolder();

        final CollectionFacet collectionFacet = new JavaArrayFacet(facetHolder, getAdapterManager());
        facetHolder.addFacet(collectionFacet);

        final TypeOfFacet typeOfFacet =
                new TypeOfFacetInferredFromArray(cls.getComponentType(), facetHolder, getSpecificationLoader());
        facetHolder.addFacet(typeOfFacet);
    }

    // TODO
    private Class<?> collectionElementType(final Class<?> cls) {
        return Object.class;
    }

    // //////////////////////////////////////////////////////////////
    // Dependencies (injected)
    // //////////////////////////////////////////////////////////////

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

}
