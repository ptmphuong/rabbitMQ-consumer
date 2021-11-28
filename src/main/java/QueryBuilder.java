import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class QueryBuilder {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private String query;
    private String originalQuery;
    private int qCount = 0;

    private static final String INSERT_TAIL = ";";

    public QueryBuilder(String tableName) {
        this.query = constructInsertHead(tableName);
        this.originalQuery = constructInsertHead(tableName);
    }

    private String constructInsertHead(String tableName) {
        if (tableName.equals("skiers")) return SqlScript.INSERT_INTO_SKIERS_HEAD;
        return SqlScript.INSERT_INTO_RESORTS_HEAD;
    }

    public void addInsertQuery(String values) {
        writeLock.lock();
        try {
            this.query = this.query + " " + values + ",";
            this.qCount++;

        } finally {
            writeLock.unlock();
        }
    }

    private void clearQuery() {
        writeLock.lock();
        try {
            this.query = this.originalQuery;
            this.qCount = 0;
        } finally {
            writeLock.unlock();
        }
    }

    public String getQuery() {
        readLock.lock();
        try {
            return this.query.substring(0, this.query.length() - 1) + INSERT_TAIL;
        } finally {
            readLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return this.qCount == 0;
        } finally {
            readLock.unlock();
        }
    }

    public int getCount() {
        readLock.lock();
        try {
            return this.qCount;
        } finally {
            readLock.unlock();
        }
    }

    public String getAndClearQuery() {
        String insertQuery = this.getQuery();
        this.clearQuery();
        return insertQuery;
    }
}
