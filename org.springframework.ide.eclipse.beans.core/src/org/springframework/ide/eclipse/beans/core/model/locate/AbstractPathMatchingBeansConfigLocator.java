/*******************************************************************************
 * Copyright (c) 2005, 2008 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.core.model.locate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Abstract {@link IBeansConfigLocator} implementation that uses ant file patterns to located
 * {@link IBeansConfig} candidates in the given {@link IProject}.
 * <p>
 * File patterns need to provided via the {@link #getAllowedFilePatterns()} method. To fine control
 * the matching process this implementation offers the {@link #canLocateInProject(IProject)} and
 * {@link #filterMatchingFiles(Set)}.
 * <p>
 * Root directories to recursivly search need to be provided by sub-classes by implementing the
 * {@link #getRootDirectories(IProject)} method.
 * @author Christian Dupuis
 * @since 2.0.5
 */
public abstract class AbstractPathMatchingBeansConfigLocator extends AbstractBeansConfigLocator {

	/** Internal path matcher that understands ant patterns */
	private PathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * Locates potential {@link IFile}s. Uses ant file name patterns to match all resources of a
	 * project recursively.
	 * @see #canLocateInProject(IProject)
	 * @see #filterMatchingFiles(Set)
	 * @see #getAllowedFilePatterns()
	 */
	public final Set<IFile> locateBeansConfigs(IProject project) {
		Set<IFile> files = new LinkedHashSet<IFile>();
		if (canLocateInProject(project)) {
			try {
				for (IPath rootDir : getRootDirectories(project)) {
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(
							rootDir);
					if (resource instanceof IFolder) {
						locateConfigsInFolder(files, project, (IFolder) resource, rootDir);
					}
				}
			}
			catch (JavaModelException e) {
				BeansCorePlugin.log(e);
			}
			catch (CoreException e) {
				BeansCorePlugin.log(e);
			}
		}
		return filterMatchingFiles(files);
	}

	/**
	 * Prepends '/' to the filePath and pattern if not already in place. Calls {@link #pathMatcher}
	 * with both.
	 */
	private boolean matches(String filePath, String pattern) {
		if (!filePath.startsWith("/")) {
			filePath = new StringBuilder().append("/").append(filePath).toString();
		}
		if (!pattern.startsWith("/")) {
			pattern = new StringBuilder().append("/").append(pattern).toString();
		}
		return pathMatcher.match(pattern, filePath);
	}

	/**
	 * Pre-check to make sure that this implementation supports a the given project and its type.
	 * <p>
	 * Sub-classes may wish to override this method to do own pre-checks.
	 * <p>
	 * This default implementation just checks if the given <code>project</code> is a java
	 * project.
	 * @param project the project to check if this implementation can search of {@link IBeansConfig}
	 * files
	 * @return true if the given project should be scanned for files
	 */
	protected boolean canLocateInProject(IProject project) {
		return true;
	}

	/**
	 * Sub-classes may override this method to provide additional locating logic.
	 * <p>
	 * This default implementation is just empty.
	 * @param files the already located files
	 * @param javaProject the current java project
	 * @param file the file we are currently looking at
	 */
	protected void doLocateConfig(Set<IFile> files, IProject project, IFile file) {
		// no-op
	}

	/**
	 * Filter path matching files. This method provides a post-processing hook for filtering found
	 * files. Sub-classes might want to override this method to filter out any un-wanted files.
	 * <p>
	 * This default implementation just passes through the given set of files.
	 * @param files the files to filter
	 * @return the {@link Set} of filtered files
	 */
	protected Set<IFile> filterMatchingFiles(Set<IFile> files) {
		return files;
	}

	/**
	 * Method to provide file patterns that should be used for searching.
	 */
	protected abstract List<String> getAllowedFilePatterns();

	/**
	 * Return the root directories to search for {@link IFile} representing Spring configuration
	 * files.
	 * @param project the {@link IProject} to search.
	 * @return the {@link Set} of {@link IPath}s representing the roots to search
	 */
	protected abstract Set<IPath> getRootDirectories(IProject project);

	/**
	 * Locates matching files in the given <code>folder</code>. Walks down the file tree until no
	 * further sub-folder is found.
	 */
	protected void locateConfigsInFolder(Set<IFile> files, IProject project, IFolder folder,
			IPath rootDir) throws CoreException {
		if (folder != null && folder.exists()) {
			for (IResource resource : folder.members()) {
				if (resource instanceof IFile) {
					String filePath = removeRootDir(resource.getFullPath(), rootDir);
					for (String pattern : getAllowedFilePatterns()) {
						if (getAllowedFileExtensions().contains(resource.getFileExtension())
								&& matches(filePath, pattern)) {
							files.add((IFile) resource);
						}
						doLocateConfig(files, project, (IFile) resource);
					}
				}
				else if (resource instanceof IFolder) {
					locateConfigsInFolder(files, project, (IFolder) resource, rootDir);
				}
			}
		}
	}

	/**
	 * Remove the given root directory path from the file path and returns the String.
	 */
	protected String removeRootDir(IPath filePath, IPath rootDir) {
		int segCount = filePath.matchingFirstSegments(rootDir);
		return filePath.removeFirstSegments(segCount).toString();
	}

}