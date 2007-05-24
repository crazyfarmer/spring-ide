/*******************************************************************************
 * Copyright (c) 2005, 2007 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.ui.namespaces;

import org.eclipse.swt.graphics.Image;

/**
 * Interface describing a XSD namespace declaration.
 * @author Christian Dupuis
 * @since 2.0
 */
public interface INamespaceDefinition {

	String getNamespacePrefix();

	String getNamespaceURI();

	String getSchemaLocation();
	
	Image getNamespaceImage();

}