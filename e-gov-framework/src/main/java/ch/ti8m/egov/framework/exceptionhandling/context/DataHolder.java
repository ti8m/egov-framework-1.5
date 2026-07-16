package ch.ti8m.egov.framework.exceptionhandling.context;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public final class DataHolder {

    private static final ThreadLocal<DataHolder> DATA_HOLDER_THREAD_LOCAL = new ThreadLocal<>();
    private final Stack<Long> commandIdStack = new Stack<>();
    private final List<Long> commandTrace = new ArrayList<>();
    private final Map<String, RepositoryInstanceContext> repositoryInstanceContextMap = new HashMap<>();
    private String userId;
    private String filter;
    private String sorting;
    private Integer offset;
    private Integer limit;
    private Integer page = 0;
    private Integer size;
    private String search;
    private String remoteAddress;
    private Object aggregate;
    private String absoluteRequestPath;
    private UUID exceptionId;
    private Boolean debugPermission;
    private Integer count;
    private String contentLanguage;
    private FullTextSearchConfig fullTextSearchConfig = new FullTextSearchConfig();

    private DataHolder(
            final String userId,
            final String filter,
            final String sorting,
            final Integer offset,
            final Integer limit,
            final String search,
            final Object aggregate,
            final FullTextSearchConfig fullTextSearchConfig
    ) {
        this.userId = userId;
        this.filter = filter;
        this.sorting = sorting;
        this.offset = offset;
        this.limit = limit;
        this.search = search;
        this.aggregate = aggregate;
        this.fullTextSearchConfig = fullTextSearchConfig;
    }

    public static void cloneIntoCurrentThread(final DataHolder dataHolder) {
        DATA_HOLDER_THREAD_LOCAL.set(dataHolder);
    }

    public static DataHolder getDataHolder() {
        return DATA_HOLDER_THREAD_LOCAL.get();
    }

    public static void initialize(
            final String userId,
            final String filter,
            final String sorting,
            final Integer offset,
            final Integer limit,
            final String search
    ) {
        if (DATA_HOLDER_THREAD_LOCAL.get() != null) {
            if (log.isWarnEnabled()) {
            /*
                DataHolder cleanup needs to be done manually if it is not properly cleaned up by the
                ResponseContextCleanUpFilter. This warning shows that some cleanup has not been properly done leading
                to a memory leak.
             */
                log.warn("DataHolder already initialized on Thread: "
                        + Thread.currentThread().getId()
                        + "  {Current userId: " + DATA_HOLDER_THREAD_LOCAL.get().userId
                        + "  New userId: " + userId + "}."
                        + "  Please ensure that DataHolder is cleaned up properly."
                        + "  CommandId Trace: " + getCommandTraceString());
            }
            DATA_HOLDER_THREAD_LOCAL.remove();
        }

        if (log.isTraceEnabled()) {
            log.trace("Initializing DataHolder for user: " + userId
                    + "  on Thread: " + Thread.currentThread().getId());
        }
        DATA_HOLDER_THREAD_LOCAL.set(new DataHolder(
                userId,
                filter,
                sorting,
                offset,
                limit,
                search,
                null,
                new FullTextSearchConfig()
        ));
    }

    public static void initialize(
            final String userId,
            final String filter,
            final String sorting,
            final Integer offset,
            final Integer limit,
            final Integer page,
            final Integer size,
            final String search,
            final String remoteAddress,
            final String absoluteRequestPath
    ) {
        initialize(userId, filter, sorting, offset, limit, search);
        DATA_HOLDER_THREAD_LOCAL.get().remoteAddress = remoteAddress;
        DATA_HOLDER_THREAD_LOCAL.get().absoluteRequestPath = absoluteRequestPath;
        setSize(size);
        setPage(page);
    }

    private static void initialize() {
        if (DATA_HOLDER_THREAD_LOCAL.get() == null) {
            DATA_HOLDER_THREAD_LOCAL.set(new DataHolder());
        }
    }

    public static void cleanUp() {
        // Cleans the ThreadLocal environment as Java reuses Threads.
        // This ensures, that each request must build its own ThreadLocal context
        if (log.isDebugEnabled()) {
            log.debug("Cleaning up DataHolder on Thread: " + Thread.currentThread().getId());
        }
        try {
            DATA_HOLDER_THREAD_LOCAL.remove();
        } catch (final Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Could not remove DataHolder.", e);
            }
        }
    }

    public static String getUserId() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().userId;
    }

    public static void setUserId(final String userId) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().userId = userId;
    }

    public static void setUserId(final Long userId) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().userId = String.valueOf(userId);
    }

    public static UUID getExceptionId() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().exceptionId;
    }

    public static void setExceptionId(final UUID uuid) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().exceptionId = uuid;
    }

    public static Boolean getDebugPermission() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().debugPermission;
    }

    public static void setDebugPermission(final Boolean debugPermission) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().debugPermission = debugPermission;
    }

    public static String getFilter() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().filter;
    }

    public static void setFilter(final String filter) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().filter = filter;
    }

    public static String getSorting() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().sorting;
    }

    public static void setSorting(final String sorting) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().sorting = sorting;
    }

    public static Integer getOffset() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().offset;
    }

    public static void setOffset(final Integer offset) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().offset = offset;
    }

    public static Integer getLimit() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().limit;
    }

    public static void setLimit(final Integer limit) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().limit = limit;
    }

    public static Integer getPage() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().page;
    }

    public static void setPage(final Integer page) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().page = page;
        updateOffsetLimit();
    }

    public static Integer getSize() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().size;
    }

    public static void setSize(final Integer size) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().size = size;
        updateOffsetLimit();
    }

    private static void updateOffsetLimit() {
        initialize();
        if (DATA_HOLDER_THREAD_LOCAL.get().size != null && DATA_HOLDER_THREAD_LOCAL.get().page != null) {
            DataHolder.setLimit(DATA_HOLDER_THREAD_LOCAL.get().size);
            DataHolder.setOffset(DATA_HOLDER_THREAD_LOCAL.get().page * DATA_HOLDER_THREAD_LOCAL.get().size);
        }
    }

    public static String getSearch() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().search;
    }

    public static FullTextSearchConfig getFullTextSearchConfig() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().fullTextSearchConfig;
    }

    public static String getRemoteAddress() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().remoteAddress;
    }

    public static <T> T getAggregate() {
        initialize();
        return (T) DATA_HOLDER_THREAD_LOCAL.get().aggregate;
    }

    public static void setAggregate(final Object aggregate) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().aggregate = aggregate;
    }

    public static Long getCurrentCommandId() {
        initialize();
        if (DATA_HOLDER_THREAD_LOCAL.get().commandIdStack.isEmpty()) {
            return -1L;
        }
        return DATA_HOLDER_THREAD_LOCAL.get().commandIdStack.peek();
    }

    public static void pushCommandId(final Long commandId) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().commandIdStack.push(commandId);
        DATA_HOLDER_THREAD_LOCAL.get().commandTrace.add(commandId);
    }

    public static void popCommandId() {
        initialize();
        if (!DATA_HOLDER_THREAD_LOCAL.get().commandIdStack.isEmpty()) {
            DATA_HOLDER_THREAD_LOCAL.get().commandIdStack.pop();
        }
    }

    public static String getAbsoluteRequestPath() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().absoluteRequestPath;
    }

    public static String getContentLanguage() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().contentLanguage;
    }

    public static void setContentLanguage(String contentLanguage) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().contentLanguage = contentLanguage;
    }

    public static Integer getCount() {
        initialize();
        return DATA_HOLDER_THREAD_LOCAL.get().count;
    }

    public static void setCount(final Integer count) {
        initialize();
        DATA_HOLDER_THREAD_LOCAL.get().count = count;
    }

    public static String getCommandTraceString() {
        initialize();
        return "["
                + DATA_HOLDER_THREAD_LOCAL.get().commandTrace.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(","))
                + "]";
    }

    public static RepositoryInstanceContext getRepositoryInstanceContext(final String repositoryDescriptor) {
        initialize();
        if (!DATA_HOLDER_THREAD_LOCAL.get().repositoryInstanceContextMap.containsKey(repositoryDescriptor)) {
            DATA_HOLDER_THREAD_LOCAL.get().repositoryInstanceContextMap.put(repositoryDescriptor, new RepositoryInstanceContext());
        }
        return DATA_HOLDER_THREAD_LOCAL.get().repositoryInstanceContextMap.get(repositoryDescriptor);
    }

}
