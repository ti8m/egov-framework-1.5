package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class BatchUpdateExecutor<T extends ModifiableEntity> implements Executor {
    public static final int PROGRESSBAR_STEPSIZE = 5;
    public static final double PROGRESSBAR_100PERCENT_MULTIPLIER = 100.0;
    public static final int BATCH_SIZE = 50;
    private List<Integer> idList;

    private static void awaitFinish(final ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(1, java.util.concurrent.TimeUnit.DAYS);
    }

    private static void logProgressBar(final Logger log, final int current, final int max, final String duration, final Integer from, final Integer to, final String logIdName) {
        final StringBuilder sb = new StringBuilder();
        final int percent = (int) Math.ceil(current * BatchUpdateExecutor.PROGRESSBAR_100PERCENT_MULTIPLIER / max);
        sb.setLength(0);
        sb.append("#".repeat(Math.max(0, percent / BatchUpdateExecutor.PROGRESSBAR_STEPSIZE)));
        log.info("[" + String.format("%-20s", sb) + "] " + current + "/" + max + " (" + percent + "%)  in " + duration + " done " + logIdName + ": " + from + " - " + to);
    }

    protected void runBatchJob(final Command command, final List<Integer> idList, final String logSubject, final String logIdName, final String interruptExceptionCode, final String batchUpdateErrorCode) {
        this.idList = idList;
        deactivatePermissions();
        final Instant start = Instant.now();
        Queue<Integer> errors = new ConcurrentLinkedQueue<>();
        if (this.idList != null && !this.idList.isEmpty()) {
            try {
                errors = listBasedUpdate(start, command, logSubject, logIdName);
            } catch (final InterruptedException e) {
                throw new EGovException(interruptExceptionCode, "Error updating " + logSubject + " for " + logIdName + "s : " + this.idList, e);
            }
        } else {
            this.idList = List.of();
        }
        logAndCleanUp(this.idList, start, errors, logSubject, logIdName, batchUpdateErrorCode);
        activatePermissions();
    }

    protected abstract void activatePermissions();

    protected abstract void deactivatePermissions();

    private void logAndCleanUp(final List<Integer> idList, final Instant start, final Queue<Integer> errors, final String logSubject, final String logIdName, final String batchUpdateErrorCode) {
        final Instant end = Instant.now();
        final Duration duration = Duration.between(start, end);
        BatchUpdateExecutor.log.info("Processed " + idList.size() + " " + logIdName + " in " + duration.toString());
        if (!errors.isEmpty()) {
            errorHandling(errors, batchUpdateErrorCode, logSubject, logIdName);
        }
    }

    private void errorHandling(final Queue<Integer> errors, final String batchUpdateErrorCode, final String logSubject, final String logIdName) {
        throw new EGovException(batchUpdateErrorCode, "Error updating " + logSubject + " for " + logIdName + "s: " + errors);
    }

    private Queue<Integer> listBasedUpdate(final Instant start, final Command command, final String logSubject, final String logIdName) throws InterruptedException {
        idList.sort(Integer::compareTo);
        final ExecutorService executorService = Executors.newWorkStealingPool(8);
        BatchUpdateExecutor.log.info("Updating " + logSubject + " for " + idList.size() + " " + logIdName + " with " + executorService);
        BatchUpdateExecutor.logProgressBar(BatchUpdateExecutor.log, 0, idList.size(), Duration.between(start, start).toString(), 0, 0, logIdName);
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final Long commandId = command.getId();
        final Queue<Integer> errors = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < idList.size(); i += BatchUpdateExecutor.BATCH_SIZE) {
            final int batchStartIndex = i;
            final String userId = DataHolder.getUserId();
            executorService.submit(() -> {
                doBatch(atomicInteger, commandId, batchStartIndex, userId, errors, logSubject, logIdName);
            });
        }
        BatchUpdateExecutor.awaitFinish(executorService);
        final Instant end = Instant.now();
        BatchUpdateExecutor.logProgressBar(BatchUpdateExecutor.log, atomicInteger.get(), idList.size(), Duration.between(start, end).toString(), idList.get(0), idList.get(idList.size() - 1), logIdName);
        return errors;
    }

    private void doBatch(final AtomicInteger atomicInteger, final Long commandId, final int batchStartIndex, final String userId, final Queue<Integer> errors, final String logSubject, final String logIdName) {
        DataHolder.setUserId(userId);
        DataHolder.pushCommandId(commandId);
        final Instant startBatch = Instant.now();
        final List<T> entities = getEntities(batchStartIndex);

        entities.forEach(entity -> {
            try {
                entityUpdate(entity);
            } catch (final Exception e) {
                BatchUpdateExecutor.log.error("Error updating " + logSubject + " for " + logIdName + " " + getId(entity), e);
                errors.add(getId(entity));
            }
        });

        persistEntities(entities);
        logBatch(atomicInteger, logIdName, startBatch, entities);
    }

    private void logBatch(final AtomicInteger atomicInteger, final String logIdName, final Instant startBatch, final List<T> entities) {
        final Instant endBatch = Instant.now();
        if (entities.size() > 0) {
            BatchUpdateExecutor.logProgressBar(BatchUpdateExecutor.log, atomicInteger.addAndGet(entities.size()), idList.size(), Duration.between(startBatch, endBatch).toString(), getId(entities.get(0)), getId(entities.get(entities.size() - 1)), logIdName);
        } else {
            BatchUpdateExecutor.logProgressBar(BatchUpdateExecutor.log, atomicInteger.addAndGet(0), idList.size(), Duration.between(startBatch, endBatch).toString(), 0, 0, logIdName);
        }
    }

    protected abstract void persistEntities(List<T> entities);

    protected abstract List<T> getEntities(int batchStartIndex);

    protected List<Integer> getIdSubList(final int batchStartIndex) {
        return idList.subList(batchStartIndex, Math.min(batchStartIndex + BATCH_SIZE, idList.size()));
    }

    protected abstract void entityUpdate(T entity);

    protected abstract Integer getId(T entity);
}
