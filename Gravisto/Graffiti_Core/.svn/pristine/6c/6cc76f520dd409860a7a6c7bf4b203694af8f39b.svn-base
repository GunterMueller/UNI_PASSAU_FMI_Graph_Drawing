// =============================================================================
//
//   BodyAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.BenchmarkCallback;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.BoundAlgorithm;
import org.graffiti.plugins.tools.benchmark.Data;
import org.graffiti.plugins.tools.benchmark.LoggingUtil;
import org.graffiti.plugins.tools.benchmark.PatchedAlgorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BodyAlgorithm extends BoundAlgorithm implements BodyElement {
    private static final Map<Class<?>, Class<? extends Algorithm>> patchMap = new HashMap<Class<?>, Class<? extends Algorithm>>();
    private static final DynamicClassLoader CLASS_LOADER = new DynamicClassLoader();

    private BodyElement nextElement;
    private InternalTimerMap internalTimerMap;
    private TimerHandle timerHandle;
    private Benchmark benchmark;
    private String resultId;

    public BodyAlgorithm(String className, Benchmark benchmark) {
        super(className);
        this.benchmark = benchmark;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(BodyElement nextElement) {
        this.nextElement = nextElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Data data, final Assignment assignment)
            throws BenchmarkException {
        String unboundVariable = findUnboundVariable(assignment);
        if (unboundVariable != null) {
            assignment.execute(unboundVariable, new BenchmarkCallback() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void call() throws BenchmarkException {
                    execute(data, assignment);
                }
            });
        } else {
            Data newData = data.copy();
            prepareTimerHandle(newData.getActiveTimers(), Collections
                    .<String> emptySet(), newData);
            try {
                AlgorithmResult result = execute(newData.getCurrentGraph(),
                        assignment);
                if (result != null && resultId != null) {
                    newData.addResult(resultId, result.getResult());
                }
            } catch (Exception e) {
                switch (benchmark.getErrorPolicy()) {
                case STOP:
                    if (e instanceof BenchmarkException)
                        throw (BenchmarkException) e;
                    else
                        throw new BenchmarkException(e);
                case WARNING:
                    e.printStackTrace();
                    LoggingUtil.getLogger().warning(
                            Benchmark.getString("warning.algorithmException", e
                                    .toString()));
                    return;
                default:
                    // IGNORE
                    break;
                }
            }
            nextElement.execute(newData, assignment);
        }
    }

    private void prepareTimerHandle(Set<String> parentActiveTimers,
            Set<String> parentSurpressedTimers, Data data)
            throws BenchmarkException {
        Set<String> activeTimers;
        Set<String> surpressedTimers;

        if (internalTimerMap == null) {
            // Outermost algorithm.

            activeTimers = parentActiveTimers;
            surpressedTimers = parentSurpressedTimers;
        } else {
            activeTimers = new HashSet<String>(parentActiveTimers);
            surpressedTimers = new HashSet<String>(parentSurpressedTimers);
            Set<String> beforeStopping = internalTimerMap.get(true, false);
            activeTimers.removeAll(beforeStopping);
            Set<String> beforeStarting = internalTimerMap.get(true, true);
            surpressedTimers.removeAll(beforeStarting);

            boolean ok = true;

            for (String id : beforeStopping) {
                ok &= surpressedTimers.add(id);
            }
            for (String id : beforeStarting) {
                ok &= activeTimers.add(id);
            }

            if (!ok)
                throw new BenchmarkException("error.timerNesting");
        }

        for (BodyAlgorithm boundAlgorithm : boundAlgorithmParameters.values()) {
            boundAlgorithm.prepareTimerHandle(activeTimers, surpressedTimers,
                    data);
        }

        timerHandle = new TimerHandle(activeTimers, surpressedTimers, data);
    }

    public void setInternalTimerMap(InternalTimerMap internalTimerMap) {
        this.internalTimerMap = internalTimerMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Algorithm create(Class<?> clazz) throws BenchmarkException {
        Algorithm algorithm;
        if (Benchmark.IS_PATCHING) {
            Class<? extends Algorithm> patchedClass = patch(clazz);
            algorithm = super.create(patchedClass);
            ((PatchedAlgorithm) algorithm)
                    .benchmark_patch$setTimerHandle(timerHandle);
        } else {
            algorithm = super.create(clazz);
        }
        return algorithm;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    private Class<? extends Algorithm> patch(Class<?> clazz) {
        // Look in the map if clazz has been patched before.
        Class<? extends Algorithm> patchedClass = patchMap.get(clazz);
        if (patchedClass != null)
            return patchedClass;

        String superClassName = clazz.getName();
        String className = superClassName + "$benchmark_patch";

        // Creates the patched class.
        ClassGen classGen = new ClassGen(
                className,
                superClassName,
                "",
                Constants.ACC_PUBLIC | Constants.ACC_SUPER,
                new String[] { "org.graffiti.plugins.tools.benchmark.PatchedAlgorithm" });

        ConstantPoolGen constantPool = classGen.getConstantPool();
        InstructionFactory factory = new InstructionFactory(classGen,
                constantPool);

        // Represents
        //
        // private TimerHandle timerHandle;
        //
        FieldGen field = new FieldGen(Constants.ACC_PRIVATE, new ObjectType(
                TimerHandle.class.getName()), "benchmark_patch$timerHandle",
                constantPool);
        classGen.addField(field.getField());

        // The default constructor.
        classGen.addEmptyConstructor(Constants.ACC_PUBLIC);

        // Represents
        //
        // @Override
        // public void benchmark_patch$setTimerHandle(TimerHandle timerHandle)
        // {
        // benchmark_patch$timerHandle = timerHandle;
        // }
        //
        InstructionList instructionList = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
                new Type[] { new ObjectType(TimerHandle.class.getName()) },
                new String[] { "arg0" }, "benchmark_patch$setTimerHandle",
                className, instructionList, constantPool);

        instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 1));
        instructionList.append(factory.createFieldAccess(className,
                "benchmark_patch$timerHandle", new ObjectType(TimerHandle.class
                        .getName()), Constants.PUTFIELD));
        instructionList.append(InstructionFactory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        classGen.addMethod(method.getMethod());
        instructionList.dispose();

        // Represents
        //
        // @Override
        // public void execute()
        // {
        // benchmark_patch$timerHandle.beforeExecute();
        // super.execute();
        // benchmark_patch$timerHandle.afterExecute();
        // }
        //
        instructionList = new InstructionList();
        method = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, Type.NO_ARGS,
                new String[] {}, "execute", className, instructionList,
                constantPool);

        instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        instructionList.append(factory.createFieldAccess(className,
                "benchmark_patch$timerHandle", new ObjectType(TimerHandle.class
                        .getName()), Constants.GETFIELD));
        instructionList.append(factory.createInvoke(
                TimerHandle.class.getName(), "beforeExecute", Type.VOID,
                Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        instructionList.append(factory.createInvoke(superClassName, "execute",
                Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        instructionList.append(factory.createFieldAccess(className,
                "benchmark_patch$timerHandle", new ObjectType(TimerHandle.class
                        .getName()), Constants.GETFIELD));
        instructionList.append(factory.createInvoke(
                TimerHandle.class.getName(), "afterExecute", Type.VOID,
                Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        instructionList.append(InstructionFactory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        classGen.addMethod(method.getMethod());
        instructionList.dispose();

        // Create and load the class.
        patchedClass = CLASS_LOADER.createClass(classGen.getJavaClass())
                .asSubclass(Algorithm.class);

        patchMap.put(clazz, patchedClass);
        return patchedClass;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
