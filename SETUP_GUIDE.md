# 🚀 Hospital Management System Setup Guide
## For JDK 25 + XAMPP

### Step 1: Start XAMPP MySQL Service

1. **Open XAMPP Control Panel**
   - Launch XAMPP from your Start menu or desktop
   - Click **"Start"** next to **MySQL**
   - Wait for the status to turn green
   - Optionally start **Apache** if you want to use phpMyAdmin

2. **Verify MySQL is Running**
   - MySQL should show "Running" status on port 3306

### Step 2: Create Database Using phpMyAdmin

1. **Open phpMyAdmin**
   - Start Apache in XAMPP (if not already started)
   - Open browser and go to: `http://localhost/phpmyadmin`
   - Or click **"Admin"** next to MySQL in XAMPP

2. **Create Database**
   - Click **"New"** in the left sidebar
   - Database name: `hospital_management`
   - Collation: `utf8_general_ci` (default)
   - Click **"Create"**

3. **Import Database Schema**
   - Select the `hospital_management` database
   - Click **"Import"** tab
   - Click **"Choose File"** and select: `database/schema.sql`
   - Click **"Go"** to execute
   - You should see success message with tables created

### Step 3: MySQL JDBC Driver (Already Downloaded)

✅ **MySQL Connector Located**
   - Your MySQL connector is already available at:
   ```
   C:\Users\Himanshu\OneDrive\Documents\java\mysql-connector-j-9.4.0\mysql-connector-j-9.4.0.jar
   ```
   - The batch files have been updated to use this path automatically

### Step 4: Update Database Configuration

The database connection is already configured for XAMPP defaults:
- Host: `localhost:3306`
- Username: `root`
- Password: `` (empty - XAMPP default)

If you've changed XAMPP MySQL password, update `DatabaseConnection.java`:
```java
private static final String PASSWORD = "your_password_here";
```

### Step 5: Compile and Run the Application

**Option A: Using Command Line**
```bash
# Navigate to project directory
cd "C:\Users\Himanshu\OneDrive\Documents\java\HospitalManagementSystem"

# Create lib directory and copy MySQL driver
mkdir lib
# Copy mysql-connector-j-8.x.x.jar to lib folder

# Compile all Java files
javac -cp "lib/*" -d build src/main/java/com/hospital/**/*.java

# Run the application
java -cp "build;lib/*" com.hospital.Main
```

**Option B: Using IDE (Recommended)**
1. Open project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Add `mysql-connector-j-8.x.x.jar` to project classpath/libraries
3. Run `Main.java`

### Step 6: Test the Application

1. **Login Screen**
   - Default credentials: `admin` / `admin123`
   - If database connection fails, check XAMPP MySQL status

2. **Dashboard**
   - Should show statistics: 5 patients, 5 doctors, 0 appointments
   - All menu buttons should be functional

3. **Test Features**
   - Add a new patient
   - Add a new doctor
   - Book an appointment
   - Search and filter records

### Troubleshooting

**Database Connection Issues:**
- Ensure XAMPP MySQL is running (green status)
- Check if database `hospital_management` exists
- Verify no firewall blocking port 3306

**JDBC Driver Issues:**
- Ensure MySQL connector JAR is in classpath
- Use the correct version (8.x.x works with all MySQL versions)

**Compilation Issues:**
- Verify JDK 25 is properly installed
- Check JAVA_HOME environment variable
- Ensure all source files are present

### Default Data

The system includes sample data:
- **Doctors**: 5 doctors across different specializations
- **Patients**: 5 sample patients
- **Users**: Admin user (admin/admin123)

### Next Steps

Once running successfully:
1. Explore all modules (Patients, Doctors, Appointments)
2. Add your own data
3. Test search and filter functionality
4. Customize the system as needed

### Quick Commands Summary

```bash
# Start XAMPP MySQL
# Open XAMPP Control Panel → Start MySQL

# Create database
# http://localhost/phpmyadmin → New → hospital_management

# Compile (from project root)
javac -cp "lib/*" -d build src/main/java/com/hospital/**/*.java

# Run
java -cp "build;lib/*" com.hospital.Main
```

---
**Need Help?** Check the main README.md for detailed feature documentation.
