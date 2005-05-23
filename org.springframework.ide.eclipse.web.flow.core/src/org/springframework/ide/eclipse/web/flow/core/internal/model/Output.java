/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.ide.eclipse.web.flow.core.internal.model;

import org.eclipse.core.resources.IResource;
import org.springframework.ide.eclipse.web.flow.core.model.ICloneableModelElement;
import org.springframework.ide.eclipse.web.flow.core.model.IModelWriter;
import org.springframework.ide.eclipse.web.flow.core.model.IOutput;
import org.springframework.ide.eclipse.web.flow.core.model.IPersistableModelElement;
import org.springframework.ide.eclipse.web.flow.core.model.IWebFlowModelElement;

public class Output extends WebFlowModelElement implements IOutput,
        IPersistableModelElement, ICloneableModelElement {

    private String name;

    private String value;

    private String as;

    private String type;

    public Output(IWebFlowModelElement parent, String name) {
        super(parent, name);
    }
    
    public Output() {
        this(null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IWebFlowModelElement#getElementType()
     */
    public int getElementType() {
        return IWebFlowModelElement.INPUT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#setName(java.lang.String)
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        super.firePropertyChange(PROPS, oldValue, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#setValue(java.lang.String)
     */
    public void setValue(String value) {
        String oldValue = this.value;
        this.value = value;
        super.firePropertyChange(PROPS, oldValue, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#setAs(java.lang.String)
     */
    public void setAs(String as) {
        String oldValue = this.as;
        this.as = as;
        super.firePropertyChange(PROPS, oldValue, as);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#setType(java.lang.String)
     */
    public void setType(String type) {
        String oldValue = this.type;
        this.type = type;
        super.firePropertyChange(PROPS, oldValue, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#getName()
     */
    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#getValue()
     */
    public String getValue() {
        return this.value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#getAs()
     */
    public String getAs() {
        return this.as;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IInput#getType()
     */
    public String getType() {
        return this.type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IWebFlowModelElement#getElementResource()
     */
    public IResource getElementResource() {
        return super.getElementParent().getElementResource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.ide.eclipse.web.flow.core.model.IPersistable#save(org.springframework.ide.eclipse.web.flow.core.model.IModelWriter)
     */
    public void save(IModelWriter writer) {
        writer.doStart(this);
        writer.doEnd(this);
    }

    /* (non-Javadoc)
     * @see org.springframework.ide.eclipse.web.flow.core.model.ICloneableModelElement#applyCloneValues(org.springframework.ide.eclipse.web.flow.core.model.ICloneableModelElement)
     */
    public void applyCloneValues(ICloneableModelElement element) {
        if (element instanceof Output) {
            Output clone = (Output) element;
            setAs(clone.getAs());
            setName(clone.getName());
            setType(clone.getType());
            setValue(clone.getValue());
        }  
    }

    /* (non-Javadoc)
     * @see org.springframework.ide.eclipse.web.flow.core.model.ICloneableModelElement#cloneModelElement()
     */
    public ICloneableModelElement cloneModelElement() {
        Output output = new Output();
        output.setElementName(getElementName());
        output.setAs(getAs());
        output.setName(getName());
        output.setType(getType());
        output.setValue(getValue());
        return output;
    }
}