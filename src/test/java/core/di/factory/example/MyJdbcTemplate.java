package core.di.factory.example;

import javax.sql.DataSource;

public class MyJdbcTemplate {

    private final DataSource dataSource;

    public MyJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
