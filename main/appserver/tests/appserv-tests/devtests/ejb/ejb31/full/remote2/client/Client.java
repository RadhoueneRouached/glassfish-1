package com.acme;


import javax.ejb.*;
import javax.annotation.*;

import javax.naming.InitialContext;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;


public class Client {

    private static SimpleReporterAdapter stat = 
        new SimpleReporterAdapter("appserv-tests");

    private static String appName;

    public static void main(String args[]) {

	appName = args[0]; 
	stat.addDescription(appName);
	Client client = new Client(args);       
        client.doTest(args[1]);	
        stat.printSummary(appName + "ID");
    }

    public Client(String[] args) {	
    }

    public void doTest(String otherApp) {

	try {

	    Hello hGlobal =  (Hello) new InitialContext().lookup("java:global/HBGlobal");
	    System.out.println("hGlobal = " + hGlobal);
	    System.out.println("hGlobal hello = " + hGlobal.hello());

	    SingletonRemote singleton = (SingletonRemote) new InitialContext().lookup("java:global/" + appName + "/SingletonBean");

	    // pass name of remotestateless app
	    singleton.doTest(otherApp);

	    stat.addStatus("local main", stat.PASS);

	} catch(Exception e) {
	    stat.addStatus("local main", stat.FAIL);
	    e.printStackTrace();
	}

    }

}
