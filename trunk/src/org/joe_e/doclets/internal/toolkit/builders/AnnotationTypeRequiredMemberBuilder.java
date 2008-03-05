/*
 * @(#)AnnotationTypeRequiredMemberBuilder.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.joe_e.doclets.internal.toolkit.builders;


import org.joe_e.doclets.internal.toolkit.*;
import org.joe_e.doclets.internal.toolkit.util.*;

import com.sun.javadoc.*;
import java.util.*;
import java.lang.reflect.*;


/**
 * Builds documentation for required annotation type members.
 *
 * This code is not part of an API.
 * It is implementation that is subject to change.
 * Do not use it as an API
 * 
 * @author Jamie Ho
 * @since 1.5
 */
public class AnnotationTypeRequiredMemberBuilder extends AbstractMemberBuilder {
    
    /**
     * The annotation type whose members are being documented.
     */
    protected ClassDoc classDoc;
    
    /**
     * The visible members for the given class.
     */
    protected VisibleMemberMap visibleMemberMap;
    
    /**
     * The writer to output the member documentation.
     */
    protected AnnotationTypeRequiredMemberWriter writer;
    
    /**
     * The list of members being documented.
     */
    protected List members;
    
    /**
     * The index of the current member that is being documented at this point 
     * in time.
     */
    protected int currentMemberIndex;
    
    /**
     * Construct a new AnnotationTypeRequiredMemberBuilder.
     *
     * @param configuration the current configuration of the
     *                      doclet.
     */
    protected AnnotationTypeRequiredMemberBuilder(Configuration configuration) {
        super(configuration);
    }
    
        
    /**
     * Construct a new AnnotationTypeMemberBuilder.
     *
     * @param configuration the current configuration of the doclet.
     * @param classDoc the class whoses members are being documented.
     * @param writer the doclet specific writer.
     */
    public static AnnotationTypeRequiredMemberBuilder getInstance(
            Configuration configuration, ClassDoc classDoc,
            AnnotationTypeRequiredMemberWriter writer) {
        AnnotationTypeRequiredMemberBuilder builder = 
            new AnnotationTypeRequiredMemberBuilder(configuration);
        builder.classDoc = classDoc;
        builder.writer = writer;
        builder.visibleMemberMap = new VisibleMemberMap(classDoc, 
            VisibleMemberMap.ANNOTATION_TYPE_MEMBER_REQUIRED, configuration.nodeprecated);
        builder.members = new ArrayList(
            builder.visibleMemberMap.getMembersFor(classDoc));
        if (configuration.getMemberComparator() != null) {
            Collections.sort(builder.members, 
                configuration.getMemberComparator());
        }
        return builder;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "AnnotationTypeRequiredMemberDetails";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invokeMethod(String methodName, Class[] paramClasses, 
            Object[] params) 
    throws Exception {
        if (DEBUG) {
            configuration.root.printError("DEBUG: " + this.getClass().getName() 
                + "." + methodName);
        }
        Method method = this.getClass().getMethod(methodName, paramClasses);
        method.invoke(this, params);
    }
    
    /**
     * Returns a list of members that will be documented for the given class.  
     * This information can be used for doclet specific documentation 
     * generation.
     *
     * @param classDoc the {@link ClassDoc} we want to check.
     * @return a list of members that will be documented.
     */
    public List members(ClassDoc classDoc) {
        return visibleMemberMap.getMembersFor(classDoc);
    }
    
    /**
     * Returns the visible member map for the members of this class.
     *
     * @return the visible member map for the members of this class.
     */
    public VisibleMemberMap getVisibleMemberMap() {
        return visibleMemberMap;
    }
    
    /**
     * summaryOrder.size()
     */
    public boolean hasMembersToDocument() {
        return members.size() > 0;
    }
    
    /**
     * Build the member documentation.
     *
     * @param elements the XML elements that specify how to construct this 
     *                documentation.
     */
    public void buildAnnotationTypeRequiredMember(List elements) {
        if (writer == null) {
            return;
        }
        for (currentMemberIndex = 0; currentMemberIndex < members.size(); 
            currentMemberIndex++) {
            build(elements);
        }
    }
    
    /**
     * Build the overall header.
     */
    public void buildHeader() {
        writer.writeHeader(classDoc, 
            configuration.getText("doclet.Annotation_Type_Member_Detail"));
    }
    
    /**
     * Build the header for the individual members.
     */
    public void buildMemberHeader() {
        writer.writeMemberHeader((MemberDoc) members.get(
                currentMemberIndex), 
            currentMemberIndex == 0); 
    }
    
    /**
     * Build the signature.
     */
    public void buildSignature() {
        writer.writeSignature((MemberDoc) members.get(currentMemberIndex));
    }
    
    /**
     * Build the deprecation information.
     */
    public void buildDeprecationInfo() {
        writer.writeDeprecated((MemberDoc) members.get(currentMemberIndex));
    }
    
    /**
     * Build the comments for the member.  Do nothing if 
     * {@link Configuration#nocomment} is set to true.
     */
    public void buildMemberComments() {
        if(! configuration.nocomment){
            writer.writeComments((MemberDoc) members.get(currentMemberIndex));
        }
    }
    
    /**
     * Build the tag information.
     */
    public void buildTagInfo() {
        writer.writeTags((MemberDoc) members.get(currentMemberIndex));
    }
    
    /**
     * Build the footer for the individual member.
     */
    public void buildMemberFooter() {
        writer.writeMemberFooter(); 
    }
    
    /**
     * Build the overall footer.
     */
    public void buildFooter() {
        writer.writeFooter(classDoc);
    }
    
    /**
     * Return the annotation type required member writer for this builder.
     *
     * @return the annotation type required member constant writer for this 
     * builder.
     */
    public AnnotationTypeRequiredMemberWriter getWriter() {
        return writer;
    }
}

