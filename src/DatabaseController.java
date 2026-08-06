public class DatabaseController {
    private Database database;
    private DatabaseView databaseView;

    public DatabaseController(Database database, DatabaseView databaseView) {
        this.database = database;
        this.databaseView = databaseView;
    }
}
