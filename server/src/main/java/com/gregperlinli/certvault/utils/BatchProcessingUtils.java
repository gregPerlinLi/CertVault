package com.gregperlinli.certvault.utils;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.executor.BatchExecutor;

import java.util.List;

/**
 * Batch processing
 *
 * @author gregperlinli
 * @version 1.0.0
 * @className BatchProcessingUtils
 * @date 2025/3/10 20:37
 */
public class BatchProcessingUtils {

    /**
     * Batch processing
     *
     * @param list       list
     * @param <T>        List element type
     * @param batchSize  Batch size
     * @param executor   Batch operation
     */
    public static <T> void batchProcess(List<T> list, int batchSize, BatchExecutor<T> executor) {
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            List<T> batch = list.subList(i, end);
            if (!executor.execute(batch)) {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "Batch operation failed, please check the data format.");
            }
        }
    }

}