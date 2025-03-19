package com.gregperlinli.certvault.executor;

import java.util.List;

/**
 * Batch Operation Executor Interface
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className BatchExecutor
 * @date 2025/3/19 23:02
 */
@FunctionalInterface
public interface BatchExecutor<T> {

    boolean execute(List<T> list);

}