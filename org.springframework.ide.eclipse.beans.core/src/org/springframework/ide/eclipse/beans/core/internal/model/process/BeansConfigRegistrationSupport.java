/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.ide.eclipse.beans.core.internal.model.process;

import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansConfig;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.ide.eclipse.beans.core.model.process.IBeansConfigRegistrationSupport;

/**
 * Utitity that provides methods to register elements with the
 * {@link BeansConfig}.
 * 
 * @author Christian Dupuis
 * @since 2.0
 * @see ReaderEventListener
 */
public class BeansConfigRegistrationSupport implements
		IBeansConfigRegistrationSupport {

	private final IBeansConfig beansConfig;

	private final ReaderEventListener readerEventListener;

	public BeansConfigRegistrationSupport(final IBeansConfig beansConfig,
			final ReaderEventListener readerEventListener) {
		this.beansConfig = beansConfig;
		this.readerEventListener = readerEventListener;
	}

	public IBeansConfig getBeansConfig() {
		return beansConfig;
	}

	public void registerAlias(AliasDefinition aliasDefinition) {
		readerEventListener.aliasRegistered(aliasDefinition);
	}

	public void registerComponent(ComponentDefinition componentDefinition) {
		readerEventListener.componentRegistered(componentDefinition);
	}
}