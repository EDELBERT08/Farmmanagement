# Liquibase Setup - Quick Reference

## ✅ What Was Configured

### 1. Production Configuration

**File**: `src/main/resources/application.properties`

```properties
spring.jpa.hibernate.ddl-auto=none
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.enabled=true
```

### 2. Test Configuration  

**File**: `src/test/resources/application.properties`

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.liquibase.enabled=false
```

> Tests use H2 in-memory database with Hibernate DDL (not Liquibase)

### 3. Changelog Files Created

```
src/main/resources/db/changelog/
├── db.changelog-master.xml          # Master changelog
└── changes/
    └── 001-initial-schema.xml       # Initial schema (6 tables + indexes)
```

## 📋 Tables Created by Liquibase

1. **field** - Farm field information
2. **crop** - Crop details with FK to field
3. **animal** - Livestock information  
4. **crop_transaction** - Financial records
5. **crop_activity** - Activity timeline
6. **user** - User authentication
7. **Indexes** - Performance optimization

## 🚀 Running the Application

### First Time (Fresh Database)

```bash
# Ensure MariaDB is running and farmdb exists
mvn spring-boot:run
```

Liquibase will automatically create all tables.

### If Tables Already Exist

See `LIQUIBASE_TROUBLESHOOTING.md` for resolution options.

## 🧪 Running Tests

```bash
mvn test
```

Tests use H2 in-memory database with Hibernate DDL (Liquibase disabled).

## 📝 Adding New Migrations

1. Create new changeset file:

   ```
   src/main/resources/db/changelog/changes/002-your-change.xml
   ```

2. Add to master changelog:

   ```xml
   <include file="db/changelog/changes/002-your-change.xml"/>
   ```

3. Restart application - Liquibase applies automatically

## 📚 Documentation Files

- **LIQUIBASE_SETUP.md** - Complete setup guide with examples
- **LIQUIBASE_TROUBLESHOOTING.md** - Solutions for common issues
- **README_LIQUIBASE.md** - This quick reference

## ⚠️ Important Notes

- **Production**: Liquibase manages schema (ddl-auto=none)
- **Tests**: Hibernate manages schema (Liquibase disabled)
- **Never modify executed changesets** - Create new ones instead
- **Each changeset runs only once** - Tracked in DATABASECHANGELOG table

## 🔍 Verification

Check if Liquibase ran successfully:

```sql
USE farmdb;
SELECT * FROM DATABASECHANGELOG;
SHOW TABLES;
```

You should see 7 changesets and 8 tables (6 app tables + 2 Liquibase tracking tables).
