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
 
package com.sun.appserv.management.util.misc;

import com.sun.appserv.management.util.stringifier.SmartStringifier;

import java.util.Arrays;
import java.util.Collection;

/**
	Various helper utilities for Collections.
 */
public final class CollectionUtil
{
		private
	CollectionUtil( )
	{
		// disallow instantiation
	}
	
        
        public static <T> void 
    addAll( Collection<T> c, T[] items )
    {
        for( final T item : items )
        {
            c.add( item );
        }
    }


	/**
		@return a String
	 */
		public static String
	toString(
		final Collection c,
		final String	 delim )
	{
	    final String[]  strings   = toStringArray( c );
	    //Arrays.sort( strings );
	    
		return StringUtil.toString( delim, (Object[])strings );
	}
	
		public static String
	toString( final Collection c )
	{
	    return toString( c, ", " );
	}

	
		public static <T> T
	getSingleton( final Collection<T> s )
	{
		if ( s.size() != 1 )
		{
			throw new IllegalArgumentException();
		}
		return( s.iterator().next() );
	}
	
	/**
		Add all items in an array to a set.
	 */
		public static <T> void
	addArray(
		final Collection<T>	c,
		final T[]		array )
	{
		for( int i = 0; i < array.length; ++i )
		{
			c.add( array[ i ] );
		}
	}
	
	/**
		@return String[]
	 */
		public static String[]
	toStringArray( final Collection	c )
	{
		final String[]	strings	= new String[ c.size() ];
		
		int	i = 0;
		for( final Object o : c )
		{
			strings[ i ]	= SmartStringifier.toString( o );
			++i;
		}
		
		return( strings );
	}
	
	/**
		@param c	the Collection
		@param elementClass	 the type of the element, must be non-primitive
		@return array of <elementClass>[] elements
	 */
		public static <T> T[]
	toArray(
		final Collection<? extends T> 	c,
		final Class<T>      	elementClass )
	{
	    final T[]  items  = ArrayUtil.newArray( elementClass, c.size() );
	    
		c.toArray( items );
		
		return items;
	}
	
	
	/**
	    @return true if all elements are String, and there is at least one element
	 */
	    public static boolean
	isAllStrings( final Collection<?> c )
	{
	    return IteratorUtil.getUniformClass( c.iterator() ) == String.class;
	}
}













