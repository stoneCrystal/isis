package test.org.nakedobjects.object;

import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectField;
import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.reflect.MemberIdentifier;
import org.nakedobjects.object.reflect.OneToOnePeer;

public class DummyOneToOneAssociation implements NakedObjectField {

    private final OneToOnePeer fieldPeer;

    public DummyOneToOneAssociation(OneToOnePeer fieldPeer) {
        this.fieldPeer = fieldPeer;}

    public OneToOnePeer getPeer() {
        return fieldPeer;        
    }
    
    public NakedObjectSpecification getSpecification() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isDerived() {
        return false;
    }

    public boolean isEmpty(NakedObject adapter) {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public boolean isMandatory() {
        return false;
    }

    public Class[] getExtensions() {
        return null;
    }

    public Naked get(NakedObject fromObject) {
        return null;
    }

    public String getLabel(NakedObject object) {
        return null;
    }

    public Object getExtension(Class cls) {
        return null;
    }

    public MemberIdentifier getIdentifier() {
        return null;
    }

    public String getLabel() {
        return null;
    }

    public String getId() {
        return fieldPeer.getIdentifier();
    }

    public boolean hasHint() {
        return false;
    }

}


/*
 * Naked Objects - a framework that exposes behaviourally complete business objects directly to the user.
 * Copyright (C) 2000 - 2005 Naked Objects Group Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address of Naked Objects Group is
 * Kingsway House, 123 Goldworth Road, Woking GU21 1NR, UK).
 */