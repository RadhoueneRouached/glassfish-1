/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.s1asdev.ejb.perf.local2;

import javax.ejb.*;


public interface BmpHome extends EJBLocalHome
{
    Bmp create(String s) throws CreateException;
    Bmp findByPrimaryKey(String s) throws CreateException;
}
