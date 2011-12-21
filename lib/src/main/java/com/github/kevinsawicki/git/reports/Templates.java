/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package com.github.kevinsawicki.git.reports;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Loader for templates
 */
public class Templates {

	/**
	 * Get reader for template
	 * 
	 * @param name
	 * @return reader
	 */
	public static Reader getTemplateReader(String name) {
		if (name == null)
			throw new IllegalArgumentException("Template name cannot be null");
		if (name.length() == 0)
			throw new IllegalArgumentException("Template name cannot be empty");

		if (!name.endsWith(".ftl"))
			name = name + ".ftl";
		return new InputStreamReader(Templates.class.getClassLoader()
				.getResourceAsStream(name));
	}

	/**
	 * Get template with name
	 * 
	 * @param name
	 * @return template
	 * @throws IOException
	 */
	public static Template getTemplate(String name) throws IOException {
		Reader reader = getTemplateReader(name);
		return new Template(name, reader, new Configuration());
	}
}
