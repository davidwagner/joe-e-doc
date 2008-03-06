/*
 * @(#)ConstructorBuilder.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.joe_e.doclets.internal.toolkit.builders;

import org.joe_e.doclets.internal.toolkit.*;
import org.joe_e.doclets.internal.toolkit.util.*;

import com.sun.javadoc.*;
import java.lang.reflect.*;
import java.util.*;


/**
 * Builds documentation for a constructor.
 *
 * This code is not part of an API.
 * It is implementation that is subject to change.
 * Do not use it as an API
 * 
 * @author Jamie Ho
 * @since 1.5
 */
public class ConstructorBuilder extends AbstractMemberBuilder {

	/**
	 * The name of this builder.
	 */
	public static final String NAME = "ConstructorDetails";

	/**
	 * The index of the current field that is being documented at this point 
	 * in time.
	 */
	private int currentMethodIndex;

	/**
	 * The class whose constructors are being documented.
	 */
	private ClassDoc classDoc;

	/**
	 * The visible constructors for the given class.
	 */
	private VisibleMemberMap visibleMemberMap;

	/**
	 * The writer to output the constructor documentation.
	 */
	private ConstructorWriter writer;

	/**
	 * The constructors being documented.
	 */
	private List constructors;

	/**
	 * Construct a new ConstructorBuilder.
	 *
	 * @param configuration the current configuration of the
	 *                      doclet.
	 */
	private ConstructorBuilder(Configuration configuration) {
		super(configuration);
	}

	/**
	 * Construct a new ConstructorBuilder.
	 *
	 * @param configuration the current configuration of the doclet.
	 * @param classDoc the class whoses members are being documented.
	 * @param writer the doclet specific writer.
	 */
	public static ConstructorBuilder getInstance(
		Configuration configuration,
		ClassDoc classDoc,
		ConstructorWriter writer) {
		ConstructorBuilder builder = new ConstructorBuilder(configuration);
		builder.classDoc = classDoc;
		builder.writer = writer;
		builder.visibleMemberMap =
			new VisibleMemberMap(
				classDoc,
				VisibleMemberMap.CONSTRUCTORS,
				configuration.nodeprecated);
		builder.constructors =
			new ArrayList(builder.visibleMemberMap.getMembersFor(classDoc));
		for (int i = 0; i < builder.constructors.size(); i++) {
			if (((ProgramElementDoc) (builder.constructors.get(i)))
				.isProtected()
				|| ((ProgramElementDoc) (builder.constructors.get(i)))
					.isPrivate()) {
				writer.setFoundNonPubConstructor(true);
			}
		}
		if (configuration.getMemberComparator() != null) {
			Collections.sort(
				builder.constructors,
				configuration.getMemberComparator());
		}
		return builder;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasMembersToDocument() {
		return constructors.size() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void invokeMethod(
		String methodName,
		Class[] paramClasses,
		Object[] params)
		throws Exception {
		if (DEBUG) {
			configuration.root.printError(
				"DEBUG: " + this.getClass().getName() + "." + methodName);
		}
		Method method = this.getClass().getMethod(methodName, paramClasses);
		method.invoke(this, params);
	}

	/**
	 * Returns a list of constructors that will be documented for the given class.  
	 * This information can be used for doclet specific documentation 
	 * generation.
	 *
	 * @return a list of constructors that will be documented.
	 */
	public List members(ClassDoc classDoc) {
		return visibleMemberMap.getMembersFor(classDoc);
	}

	/**
	 * Return the constructor writer for this builder.
	 *
	 * @return the constructor writer for this builder.
	 */
	public ConstructorWriter getWriter() {
		return writer;
	}

	/**
	 * Build the constructor documentation.
	 *
	 * @param elements the XML elements that specify how to construct this 
	 *                documentation.
	 */
	public void buildConstructorDoc(List elements) {
		if (writer == null) {
			return;
		}
		for (currentMethodIndex = 0;
			currentMethodIndex < constructors.size();
			currentMethodIndex++) {
			build(elements);
		}
	}

	/**
	 * Build the overall header.
	 */
	public void buildHeader() {
		writer.writeHeader(
			classDoc,
			configuration.getText("doclet.Constructor_Detail"));
	}

	/**
	 * Build the header for the individual constructor.
	 */
	public void buildConstructorHeader() {
		writer.writeConstructorHeader(
			(ConstructorDoc) constructors.get(currentMethodIndex),
			currentMethodIndex == 0);
	}

	/**
	 * Build the signature.
	 */
	public void buildSignature() {
		writer.writeSignature(
			(ConstructorDoc) constructors.get(currentMethodIndex));
	}

	/**
	 * Build the deprecation information.
	 */
	public void buildDeprecationInfo() {
		writer.writeDeprecated(
			(ConstructorDoc) constructors.get(currentMethodIndex));
	}

	/**
	 * Build the comments for the constructor.  Do nothing if 
	 * {@link Configuration#nocomment} is set to true.
	 */
	public void buildConstructorComments() {
		if (!configuration.nocomment) {
			writer.writeComments(
				(ConstructorDoc) constructors.get(currentMethodIndex));
		}
	}

	/**
	 * Build the tag information.
	 */
	public void buildTagInfo() {
		writer.writeTags((ConstructorDoc) constructors.get(currentMethodIndex));
	}

	/**
	 * Build the footer for the individual constructor.
	 */
	public void buildConstructorFooter() {
		writer.writeConstructorFooter();
	}

	/**
	 * Build the overall footer.
	 */
	public void buildFooter() {
		writer.writeFooter(classDoc);
	}
}