package org.glassfish.enterprise.admin.ncli;

import org.glassfish.cli.metadata.CommandDesc;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.*;

/**
 * @author &#2325;&#2375;&#2342;&#2366;&#2352 (km@dev.java.net)
 */
final class Parser {
    final Set<String> slc  = new HashSet<String>();
    final Set<String> uslc = new HashSet<String>();

    private final String[] args;

    Parser(String[] args) {
        if (args == null)
            throw new IllegalArgumentException("null arg");
        this.args = args;
        initialize();
    }

    FirstPassResult firstPass() throws ParserException {
        //TODO
        return null;
    }

    SecondPassResult secondPass(CommandDesc desc, String[] commandArguments) throws ParserException {
        //TODO
        return null;
    }

    // Private instance methods

    private void initialize() {
        initializeLegacyCommands();
    }

    private void initializeLegacyCommands() {
        ParseUtilities.file2Set(Constants.SUPPORTED_CMD_FILE_NAME, slc);
        ParseUtilities.file2Set(Constants.UNSUPPORTED_CMD_FILE_NAME, uslc);
    }

    // Private static methods
}

