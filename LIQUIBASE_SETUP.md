# Liquibase Database Migration Setup

## Overview

Liquibase has been successfully configured for the Farm Management application to manage database schema changes in a version-controlled manner.

## What Was Configured

### 1. Dependencies

**Modified**: [pom.xml](file:///C:/Users/edelbert.kipchumba/Desktop/PROJECTS/Farmmanagement/pom.xml)

- Added `liquibase-core` dependency (managed by Spring Boot parent)

### 2. Application Configuration

**Modified**: [application.properties](file:///C:/Users/edelbert.kipchumba/Desktop/PROJECTS/Farmmanagement/src/main/resources/application.properties)

```properties
spring.jpa.hibernate.ddl-auto=none  # Disabled Hibernate auto-DDL
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.enabled=true
```

### 3. Changelog Structure

Created the following directory structure:

```
src/main/resources/
└── db/
    └── changelog/
        ├── db.changelog-master.xml
        └── changes/
            └── 001-initial-schema.xml
```

## Changelog Files

### Master Changelog

**File**: [db.changelog-master.xml](file:///C:/Users/edelbert.kipchumba/Desktop/PROJECTS/Farmmanagement/src/main/resources/db/changelog/db.changelog-master.xml)

This is the main entry point that includes all migration files.

### Initial Schema Migration

**File**: [001-initial-schema.xml](file:///C:/Users/edelbert.kipchumba/Desktop/PROJECTS/Farmmanagement/src/main/resources/db/changelog/changes/001-initial-schema.xml)

Contains 7 changesets:

1. **field** table - Farm field information
2. **crop** table - Crop details with foreign key to field
3. **animal** table - Livestock information
4. **crop_transaction** table - Financial records for crops
5. **crop_activity** table - Activity log for crops
6. **user** table - User authentication
7. **Indexes** - Performance optimization indexes

## How Liquibase Works

1. **On Application Startup**: Liquibase automatically runs and checks the `DATABASECHANGELOG` table
2. **Tracks Changes**: Each changeset has a unique ID and is executed only once
3. **Rollback Support**: Liquibase can rollback changes if needed
4. **Version Control**: All schema changes are tracked in XML files

## Adding New Migrations

### Step 1: Create a New Changelog File

Create a new file in `src/main/resources/db/changelog/changes/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="002-add-crop-variety-column" author="your-name">
        <addColumn tableName="crop">
            <column name="variety" type="VARCHAR(255)"/>
        </addColumn>
    </changeSet>

</databaseChangeLog>
```

### Step 2: Include in Master Changelog

Add the new file to `db.changelog-master.xml`:

```xml
<include file="db/changelog/changes/001-initial-schema.xml"/>
<include file="db/changelog/changes/002-add-crop-variety.xml"/>
```

### Step 3: Restart Application

Liquibase will automatically apply the new migration on startup.

## Common Liquibase Operations

### Add a Column

```xml
<changeSet id="unique-id" author="author-name">
    <addColumn tableName="table_name">
        <column name="column_name" type="VARCHAR(255)"/>
    </addColumn>
</changeSet>
```

### Modify a Column

```xml
<changeSet id="unique-id" author="author-name">
    <modifyDataType tableName="table_name" 
                    columnName="column_name" 
                    newDataType="TEXT"/>
</changeSet>
```

### Add an Index

```xml
<changeSet id="unique-id" author="author-name">
    <createIndex indexName="idx_name" tableName="table_name">
        <column name="column_name"/>
    </createIndex>
</changeSet>
```

### Insert Data

```xml
<changeSet id="unique-id" author="author-name">
    <insert tableName="user">
        <column name="username" value="admin"/>
        <column name="password" value="$2a$10$..."/>
        <column name="role" value="ADMIN"/>
    </insert>
</changeSet>
```

## Liquibase Tables

Liquibase creates two tables in your database:

- **DATABASECHANGELOG**: Tracks which changesets have been executed
- **DATABASECHANGELOGLOCK**: Prevents concurrent migrations

## Important Notes

> [!IMPORTANT]
>
> - **Never modify executed changesets** - Create new ones instead
> - **Use unique IDs** - Each changeset must have a unique ID
> - **Test migrations** - Always test on a development database first

> [!WARNING]
>
> - Setting `spring.jpa.hibernate.ddl-auto=none` means Hibernate will NOT auto-create tables
> - All schema changes MUST be done through Liquibase migrations
> - Existing data will be preserved when Liquibase runs

## Running the Application

### First Time Setup

1. Ensure your MariaDB database `farmdb` exists
2. Run the application:

   ```bash
   mvn spring-boot:run
   ```

3. Liquibase will create all tables automatically

### Subsequent Runs

- Liquibase checks for new migrations and applies only what hasn't been executed
- No manual SQL scripts needed

## Rollback (Advanced)

To rollback the last changeset:

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Verification

After running the application, check your database:

```sql
-- View Liquibase changelog
SELECT * FROM DATABASECHANGELOG;

-- Verify tables were created
SHOW TABLES;

-- Check table structure
DESCRIBE crop;
```

## Troubleshooting

### Issue: "Table already exists"

**Solution**: If tables already exist from Hibernate auto-DDL:

1. Backup your data
2. Drop all tables
3. Restart the application to let Liquibase create them

### Issue: "Changeset already executed"

**Solution**: Check the `DATABASECHANGELOG` table. If a changeset is marked as executed but you want to re-run it, you can manually delete the row (use with caution).

### Issue: "Liquibase not running"

**Solution**: Verify `spring.liquibase.enabled=true` in `application.properties`

## Best Practices

1. **One Change Per Changeset**: Keep changesets focused on a single logical change
2. **Descriptive IDs**: Use descriptive changeset IDs like `001-create-user-table`
3. **Author Attribution**: Always include the author attribute
4. **Test First**: Test migrations on development before production
5. **Version Control**: Commit changelog files with your code
6. **Sequential Naming**: Use sequential numbers for changelog files (001, 002, 003...)

## Next Steps

- Add seed data migrations for initial users
- Create migrations for any future schema changes
- Consider adding rollback tags for production deployments
- Set up different changelogs for different environments (dev, staging, prod)
