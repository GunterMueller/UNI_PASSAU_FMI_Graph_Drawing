package org.graffiti.plugins.scripting.delegates;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.graffiti.plugins.scripting.ConsoleOutput;
import org.graffiti.plugins.scripting.ConsoleProvider;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.FieldAccess;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted The console.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class ConsoleDelegate extends ObjectDelegate {
    @ScriptedField(access = FieldAccess.Get)
    protected BlackBoxDelegate<PrintStream> out;

    @ScriptedField(access = FieldAccess.Get)
    protected BlackBoxDelegate<PrintStream> err;

    public ConsoleDelegate(Scope scope, ConsoleProvider consoleProvider) {
        super(scope);
        out = createStream(consoleProvider, ConsoleOutput.Standard);
        err = createStream(consoleProvider, ConsoleOutput.Error);
    }

    private BlackBoxDelegate<PrintStream> createStream(
            final ConsoleProvider consoleProvider, final ConsoleOutput kind) {
        return new BlackBoxDelegate<PrintStream>(new PrintStream(
                new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        consoleProvider.print(String.valueOf((char) b), kind);
                    }
                }));
    }

    @Override
    public String toString() {
        return "[Console]";
    }
}
