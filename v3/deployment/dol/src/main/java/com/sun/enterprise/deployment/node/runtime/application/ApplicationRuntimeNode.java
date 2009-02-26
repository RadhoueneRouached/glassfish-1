/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */


package com.sun.enterprise.deployment.node.runtime.application;

import com.sun.enterprise.deployment.Application;
import com.sun.enterprise.deployment.Role;
import com.sun.enterprise.deployment.interfaces.SecurityRoleMapper;
import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.node.runtime.RuntimeBundleNode;
import com.sun.enterprise.deployment.node.runtime.common.SecurityRoleMappingNode;
import com.sun.enterprise.deployment.runtime.common.PrincipalNameDescriptor;
import com.sun.enterprise.deployment.runtime.common.SecurityRoleMapping;
import com.sun.enterprise.deployment.util.ModuleDescriptor;
import com.sun.enterprise.deployment.util.XModuleType;
import com.sun.enterprise.deployment.xml.DTDRegistry;
import com.sun.enterprise.deployment.xml.RuntimeTagNames;
import org.glassfish.security.common.Group;
import org.w3c.dom.Node;

import javax.enterprise.deploy.shared.ModuleType;
import java.util.List;
import java.util.Map;

/**
 * This node handles all runtime-information pertinent to applications
 * The reading needs to be backward compatible with J2EE 1.2 and 1.3
 * where all runtime information was saved at the .ear file level in an
 * unique sun-ri.xml file. In J2EE 1.4, each sub archivist is responsible
 * for saving its runtime-info at his level.
 *
 * @author  Jerome Dochez
 * @version 
 */
public class ApplicationRuntimeNode extends RuntimeBundleNode<Application> {
    
    private Application descriptor=null;
    private String currentWebUri=null;
    
    public ApplicationRuntimeNode(Application descriptor) {
        super(descriptor);
	this.descriptor=descriptor;	
    }   
    
    /**
     * Initialize the child handlers
     */    
    protected void Init() {     
        super.Init();                          
        registerElementHandler(new XMLElement(RuntimeTagNames.SECURITY_ROLE_MAPPING), 
                               SecurityRoleMappingNode.class);              
    }
        
   /**
    * register this node as a root node capable of loading entire DD files
    * 
    * @param publicIDToDTD is a mapping between xml Public-ID to DTD 
    * @return the doctype tag name
    */
   public static String registerBundle(Map publicIDToDTD) {    
       publicIDToDTD.put(DTDRegistry.SUN_APPLICATION_130_DTD_PUBLIC_ID, DTDRegistry.SUN_APPLICATION_130_DTD_SYSTEM_ID);
       publicIDToDTD.put(DTDRegistry.SUN_APPLICATION_140_DTD_PUBLIC_ID, DTDRegistry.SUN_APPLICATION_140_DTD_SYSTEM_ID);       
       publicIDToDTD.put(DTDRegistry.SUN_APPLICATION_141_DTD_PUBLIC_ID, DTDRegistry.SUN_APPLICATION_141_DTD_SYSTEM_ID);       
       publicIDToDTD.put(DTDRegistry.SUN_APPLICATION_500_DTD_PUBLIC_ID, DTDRegistry.SUN_APPLICATION_500_DTD_SYSTEM_ID);       
       if (!restrictDTDDeclarations()) {
           publicIDToDTD.put(DTDRegistry.SUN_APPLICATION_140beta_DTD_PUBLIC_ID, DTDRegistry.SUN_APPLICATION_140beta_DTD_SYSTEM_ID);       
       }
       return RuntimeTagNames.S1AS_APPLICATION_RUNTIME_TAG;
   }   
    
    /**
     * @return the XML tag associated with this XMLNode
     */
    protected XMLElement getXMLRootTag() {
        return new XMLElement(RuntimeTagNames.S1AS_APPLICATION_RUNTIME_TAG);
    } 
    
    /** 
     * @return the DOCTYPE that should be written to the XML file
     */
    public String getDocType() {
	return DTDRegistry.SUN_APPLICATION_500_DTD_PUBLIC_ID;
    }
    
    /**
     * @return the SystemID of the XML file
     */
    public String getSystemID() {
	return DTDRegistry.SUN_APPLICATION_500_DTD_SYSTEM_ID;
    }

    /**
     * @return NULL for all runtime nodes.
     */
    public List<String> getSystemIDs() {
        return null;
    }
    
    /**
     * all sub-implementation of this class can use a dispatch table to map xml element to
     * method name on the descriptor class for setting the element value. 
     *  
     * @return the map with the element name as a key, the setter method as a value
     */    
    protected Map getDispatchTable() {    
        Map table = super.getDispatchTable();
        table.put(RuntimeTagNames.REALM, "setRealm");
        return table;
    }
    
    /**
     * receives notification of the value for a particular tag
     * 
     * @param element the xml element
     * @param value it's associated value
     */
    public void setElementValue(XMLElement element, String value) {
	if (element.getQName().equals(RuntimeTagNames.PASS_BY_REFERENCE)) {
	    descriptor.setPassByReference("true".equalsIgnoreCase(value));
	} else 
	if (element.getQName().equals(RuntimeTagNames.UNIQUE_ID)) {
	    descriptor.setUniqueId(Long.parseLong(value));
	} else
	if (element.getQName().equals(RuntimeTagNames.WEB_URI)) {
	    currentWebUri=value;
	} else 
	if (element.getQName().equals(RuntimeTagNames.CONTEXT_ROOT)) {
	    if (currentWebUri!=null) {
		ModuleDescriptor md = descriptor.getModuleDescriptorByUri(currentWebUri);
                if (md==null) {
                    throw new RuntimeException("No bundle in application with uri " + currentWebUri);
                }
		currentWebUri=null;
		if (md.getModuleType().equals(XModuleType.WAR)) {
		    md.setContextRoot(value);
		} else {
		    throw new RuntimeException(currentWebUri + " uri does not point to a web bundle");
		} 
	    } else {
		throw new RuntimeException("No uri provided for this context-root " + value);
	    }
	} else super.setElementValue(element, value);
    }

    /**
     * Adds  a new DOL descriptor instance to the descriptor instance associated with 
     * this XMLNode
     *
     * @param mewDescriptor the new descriptor
     */
    public void addDescriptor(Object newDescriptor) {
        if (newDescriptor instanceof SecurityRoleMapping) {
            SecurityRoleMapping roleMap = (SecurityRoleMapping) newDescriptor;
            descriptor.addSecurityRoleMapping(roleMap);
            if (descriptor!=null && !descriptor.isVirtual()) {
                Role role = new Role(roleMap.getRoleName());
                SecurityRoleMapper rm = descriptor.getRoleMapper();
                if (rm != null) {
                    List<PrincipalNameDescriptor> principals = roleMap.getPrincipalNames();
                    for (int i = 0; i < principals.size(); i++) {
                        rm.assignRole(principals.get(i).getPrincipal(),
                            role, descriptor);
                    }
                    List<String> groups = roleMap.getGroupNames();
                    for (int i = 0; i < groups.size(); i++) {
                        rm.assignRole(new Group(groups.get(i)),
                            role, descriptor);
                    }
                }
            }
        }
    }
    
    
    /**
     * write the descriptor class to a DOM tree and return it
     *
     * @param parent node for the DOM tree
     * @param nodeName the node name
     * @param descriptor the descriptor to write
     * @return the DOM tree top node
     */    
    public Node writeDescriptor(Node parent, String nodeName, Application application) {    
        Node appNode = super.writeDescriptor(parent, nodeName, application);
	
        // web*
	for (ModuleDescriptor module : application.getModules()) {
	    if (module.getModuleType().equals(XModuleType.WAR)) {
		Node web = appendChild(appNode, RuntimeTagNames.WEB);
		appendTextChild(web, RuntimeTagNames.WEB_URI, module.getArchiveUri());
		appendTextChild(web, RuntimeTagNames.CONTEXT_ROOT, module.getContextRoot());
	    }
	}
	
	// pass-by-reference ?
	if (application.isPassByReferenceDefined()) {
	    appendTextChild(appNode, RuntimeTagNames.PASS_BY_REFERENCE, String.valueOf(application.getPassByReference()));
	}
	
	// unique-id
	appendTextChild(appNode, RuntimeTagNames.UNIQUE_ID, String.valueOf(application.getUniqueId()));
	
        // security-role-mapping*
        List<SecurityRoleMapping> roleMappings = application.getSecurityRoleMappings();
        for (int i = 0; i < roleMappings.size(); i++) { 
            SecurityRoleMappingNode srmn = new SecurityRoleMappingNode();
            srmn.writeDescriptor(appNode, RuntimeTagNames.SECURITY_ROLE_MAPPING, roleMappings.get(i));
        }
        
        // realm?
        appendTextChild(appNode, RuntimeTagNames.REALM, application.getRealm());
        
        return appNode;
    }
}
