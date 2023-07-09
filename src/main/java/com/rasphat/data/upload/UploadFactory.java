package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;

/**
 * The UploadFactory class provides a method for obtaining an appropriate UploadProcessor based on the specified project.
 */
public class UploadFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(UploadFactory.class);

    /**
     * Returns an appropriate UploadProcessor based on the specified project.
     *
     * @param project The project name for which the UploadProcessor is needed.
     * @return An UploadProcessor instance corresponding to the project.
     * @throws IllegalArgumentException If the argument contains no valid project.
     */
    public UploadProcessor getUploadProcessor(String project) throws IllegalArgumentException {

        Map<String, Supplier<UploadProcessor>> processorMap = new HashMap<>();
        processorMap.put(UploadType.TENEO.name().toLowerCase(), UploadTeneo::new);
        processorMap.put(UploadType.TENEO_TREATMENTS.name().toLowerCase(), UploadTeneoTreatments::new);
        processorMap.put(UploadType.VICTUS.name().toLowerCase(), UploadVictus::new);
        processorMap.put(UploadType.ZDW3.name().toLowerCase(), UploadZdw3::new);

        Supplier<UploadProcessor> processorSupplier = processorMap.get(project.toLowerCase());
        if (processorSupplier != null) {
            return processorSupplier.get();
        } else {
            LOGGER.error("Exception in {}, method: {}(), line: {}",
                    getClass().toString(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()
            );
            LOGGER.error("Unsupported project: " + project);
            throw new IllegalArgumentException("Unsupported project: " + project);
        }
    }
}