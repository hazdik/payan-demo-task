# Setup Guide for Payan Demo Task

## Prerequisites Installation

### Windows Installation

#### 1. Install Java 17
1. Download Java 17 JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)
2. Run the installer and follow the installation wizard
3. Set JAVA_HOME environment variable:
   - Open "Environment Variables" in Windows
   - Add new System Variable: `JAVA_HOME` = `C:\Program Files\Java\jdk-17` (adjust path as needed)
   - Add to PATH: `%JAVA_HOME%\bin`
4. Verify installation:
   ```cmd
   java -version
   ```

#### 2. Install Maven
1. Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Set M2_HOME environment variable:
   - Add new System Variable: `M2_HOME` = `C:\Program Files\Apache\maven`
   - Add to PATH: `%M2_HOME%\bin`
4. Verify installation:
   ```cmd
   mvn -version
   ```

### Alternative: Using Maven Wrapper (Recommended)

If Maven is not installed, you can use the Maven Wrapper included in most Spring Boot projects.

## Building the Project

### Option 1: Using Maven (if installed)
```bash
mvn clean install
```

### Option 2: Using Maven Wrapper (Windows)
```cmd
mvnw.cmd clean install
```

### Option 3: Using Maven Wrapper (Unix/Mac)
```bash
./mvnw clean install
```

### Option 4: Using Your IDE
- **IntelliJ IDEA**: Right-click on `pom.xml` → Run Maven → clean install
- **Eclipse**: Right-click on project → Run As → Maven build
- **VS Code**: Use the Maven extension

## Running the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using Maven Wrapper (Windows)
```cmd
mvnw.cmd spring-boot:run
```

### Option 3: Using Maven Wrapper (Unix/Mac)
```bash
./mvnw spring-boot:run
```

### Option 4: Running the JAR file
```bash
java -jar target/payan-demo-task-1.0.0.jar
```

### Option 5: Using Your IDE
- **IntelliJ IDEA**: Right-click on `PayanDemoApplication.java` → Run
- **Eclipse**: Right-click on `PayanDemoApplication.java` → Run As → Java Application
- **VS Code**: Open `PayanDemoApplication.java` and click "Run" above the main method

## Accessing the Application

Once the application is running, you should see console output indicating the server has started.

1. Open your web browser
2. Navigate to: `http://localhost:8080/login`
3. Use one of the demo credentials:
   - Username: `admin` / Password: `admin123`
   - Username: `user1` / Password: `password123`

## Testing the Application

### Manual Testing Steps

1. **Login Test**:
   - Go to `http://localhost:8080/login`
   - Enter valid credentials
   - Verify redirect to dashboard

2. **Dashboard Test**:
   - Verify transactions table is displayed
   - Check that 15 sample transactions are shown
   - Verify transaction details (amounts, types, statuses)

3. **Logout Test**:
   - Click the "Logout" button
   - Verify redirect to login page
   - Verify logout message is displayed

4. **Security Test**:
   - Try accessing `http://localhost:8080/dashboard` without logging in
   - Verify redirect to login page

5. **Invalid Login Test**:
   - Enter incorrect credentials
   - Verify error message is displayed

## Troubleshooting

### Port Already in Use
```
Error: Web server failed to start. Port 8080 was already in use.
```
**Solution**: Change the port in `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Java Version Issues
```
Error: Java version mismatch
```
**Solution**: 
- Check your Java version: `java -version`
- Ensure Java 17 or higher is installed
- Update JAVA_HOME environment variable

### Maven Command Not Found
```
Error: 'mvn' is not recognized as an internal or external command
```
**Solution**: 
- Install Maven following the guide above, OR
- Use Maven Wrapper (mvnw.cmd on Windows, ./mvnw on Unix/Mac)

### Lombok-related Compilation Errors
**Solution**: 
- Ensure your IDE has the Lombok plugin installed
- For IntelliJ IDEA: File → Settings → Plugins → Search for "Lombok" → Install
- For Eclipse: Download lombok.jar from projectlombok.org and run it

### Database Connection Issues
The application uses H2 in-memory database, which should work out of the box. If you encounter issues:
- Check the H2 console at `http://localhost:8080/h2-console`
- Use JDBC URL: `jdbc:h2:mem:payandemo`
- Username: `sa`
- Password: (leave empty)

## Development Tips

### Hot Reload
The project includes Spring Boot DevTools for automatic restart on code changes:
- Make changes to your code
- Save the file
- The application will automatically restart

### Viewing Database
Access the H2 console while the application is running:
1. Go to `http://localhost:8080/h2-console`
2. Enter JDBC URL: `jdbc:h2:mem:payandemo`
3. Click "Connect"
4. You can now view and query the database tables

### Adding New Transactions
To add more sample transactions, edit:
`src/main/java/com/payan/demo/config/DataLoader.java`

### Modifying UI
Edit the Thymeleaf templates:
- Login page: `src/main/resources/templates/login.html`
- Dashboard: `src/main/resources/templates/dashboard.html`

## Next Steps

After successfully running the application, you can:
1. Explore the codebase
2. Add new features (e.g., add/edit/delete transactions)
3. Customize the UI
4. Add more user roles and permissions
5. Integrate with a persistent database (MySQL, PostgreSQL)
6. Add API endpoints for external integrations
7. Implement additional security features

## Support

For issues or questions:
1. Check the main README.md file
2. Review the troubleshooting section above
3. Check Spring Boot documentation: https://spring.io/projects/spring-boot
4. Check Thymeleaf documentation: https://www.thymeleaf.org/
