# Liquibase Migration Error - Tables Already Exist

## Problem

The application is failing to start because Liquibase is trying to create tables that already exist in the database. This happened because:

1. Previously, Hibernate was managing the schema (`spring.jpa.hibernate.ddl-auto=update`)
2. Tables were already created by Hibernate
3. Now Liquibase is trying to create the same tables and failing

## Solution Options

### Option 1: Fresh Start (Recommended for Development)

Drop all existing tables and let Liquibase recreate them from scratch.

**Steps:**

1. Connect to your MariaDB database:

   ```bash
   mysql -u root -p
   ```

2. Drop all tables:

   ```sql
   USE farmdb;
   
   SET FOREIGN_KEY_CHECKS = 0;
   DROP TABLE IF EXISTS crop_activity;
   DROP TABLE IF EXISTS crop_transaction;
   DROP TABLE IF EXISTS crop;
   DROP TABLE IF EXISTS animal;
   DROP TABLE IF EXISTS field;
   DROP TABLE IF EXISTS user;
   SET FOREIGN_KEY_CHECKS = 1;
   
   -- Verify tables are gone
   SHOW TABLES;
   ```

3. Exit MySQL and run the application:

   ```bash
   mvn spring-boot:run
   ```

Liquibase will now create all tables fresh.

---

### Option 2: Mark Changesets as Already Executed

Tell Liquibase that the changesets have already been applied (without actually running them).

**Steps:**

1. Temporarily disable Liquibase in `application.properties`:

   ```properties
   spring.liquibase.enabled=false
   ```

2. Start the application once to ensure it works

3. Manually insert records into Liquibase's tracking table:

   ```sql
   USE farmdb;
   
   -- Create Liquibase tracking tables if they don't exist
   CREATE TABLE IF NOT EXISTS DATABASECHANGELOG (
     ID VARCHAR(255) NOT NULL,
     AUTHOR VARCHAR(255) NOT NULL,
     FILENAME VARCHAR(255) NOT NULL,
     DATEEXECUTED DATETIME NOT NULL,
     ORDEREXECUTED INT NOT NULL,
     EXECTYPE VARCHAR(10) NOT NULL,
     MD5SUM VARCHAR(35),
     DESCRIPTION VARCHAR(255),
     COMMENTS VARCHAR(255),
     TAG VARCHAR(255),
     LIQUIBASE VARCHAR(20),
     CONTEXTS VARCHAR(255),
     LABELS VARCHAR(255),
     DEPLOYMENT_ID VARCHAR(10)
   );
   
   -- Mark all changesets as executed
   INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, EXECTYPE, MD5SUM, DESCRIPTION, COMMENTS, TAG, LIQUIBASE, DEPLOYMENT_ID)
   VALUES 
   ('001-create-field-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 1, 'EXECUTED', NULL, 'createTable tableName=field', '', NULL, '4.20', NULL),
   ('002-create-crop-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 2, 'EXECUTED', NULL, 'createTable tableName=crop', '', NULL, '4.20', NULL),
   ('003-create-animal-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 3, 'EXECUTED', NULL, 'createTable tableName=animal', '', NULL, '4.20', NULL),
   ('004-create-crop-transaction-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 4, 'EXECUTED', NULL, 'createTable tableName=crop_transaction', '', NULL, '4.20', NULL),
   ('005-create-crop-activity-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 5, 'EXECUTED', NULL, 'createTable tableName=crop_activity', '', NULL, '4.20', NULL),
   ('006-create-user-table', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 6, 'EXECUTED', NULL, 'createTable tableName=user', '', NULL, '4.20', NULL),
   ('007-add-indexes', 'farm-admin', 'db/changelog/changes/001-initial-schema.xml', NOW(), 7, 'EXECUTED', NULL, 'createIndex indexName=idx_crop_field, tableName=crop; createIndex indexName=idx_transaction_crop, tableName=crop_transaction; createIndex indexName=idx_activity_crop, tableName=crop_activity; createIndex indexName=idx_user_username, tableName=user', '', NULL, '4.20', NULL);
   
   -- Verify
   SELECT * FROM DATABASECHANGELOG;
   ```

4. Re-enable Liquibase in `application.properties`:

   ```properties
   spring.liquibase.enabled=true
   ```

5. Restart the application

---

### Option 3: Use Liquibase's changeLogSync (Cleanest)

Use Liquibase Maven plugin to sync the changelog without executing it.

**Steps:**

1. Add this to your `pom.xml` temporarily (inside `<build><plugins>` section):

   ```xml
   <plugin>
       <groupId>org.liquibase</groupId>
       <artifactId>liquibase-maven-plugin</artifactId>
       <version>4.29.2</version>
       <configuration>
           <propertyFile>src/main/resources/liquibase.properties</propertyFile>
       </configuration>
   </plugin>
   ```

2. Create `src/main/resources/liquibase.properties`:

   ```properties
   changeLogFile=db/changelog/db.changelog-master.xml
   url=jdbc:mariadb://localhost:3306/farmdb?allowPublicKeyRetrieval=true
   username=root
   password=your_password_here
   driver=org.mariadb.jdbc.Driver
   ```

3. Run the sync command:

   ```bash
   mvn liquibase:changelogSync
   ```

4. Remove the temporary plugin and properties file

5. Run the application:

   ```bash
   mvn spring-boot:run
   ```

---

## Recommended Approach

**For Development**: Use **Option 1** (Fresh Start)

- Simplest and cleanest
- Ensures Liquibase has full control
- No risk of schema drift

**For Production**: Use **Option 3** (changeLogSync)

- Preserves existing data
- Professional approach
- Proper Liquibase tracking

---

## Verification

After applying any solution, verify Liquibase is working:

1. Check the tracking table:

   ```sql
   SELECT * FROM DATABASECHANGELOG;
   ```

2. Verify all 7 changesets are listed

3. Application should start without errors

---

## Future Migrations

Once this is resolved, you can add new migrations normally:

1. Create a new changeset file (e.g., `002-add-crop-variety.xml`)
2. Include it in `db.changelog-master.xml`
3. Restart the application
4. Liquibase will apply only the new changeset

---

## Quick Fix Command (Option 1)

If you want to quickly reset everything:

```bash
# Connect to MySQL
mysql -u root -p

# Run these commands
USE farmdb;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS crop_activity, crop_transaction, crop, animal, field, user, DATABASECHANGELOG, DATABASECHANGELOGLOCK;
SET FOREIGN_KEY_CHECKS = 1;
EXIT;

# Then run the application
mvn spring-boot:run
```

This will give you a completely fresh start with Liquibase managing everything.
