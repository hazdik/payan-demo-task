# Access Control & Security Configuration

## 🔒 Role-Based Access Control

### User Roles

The application has two roles:
- **USER** - Regular user with limited access
- **ADMIN** - Administrator with full access

### Access Matrix

| Page/Endpoint | USER | ADMIN |
|--------------|------|-------|
| `/login` | ✅ Public | ✅ Public |
| `/dashboard` | ✅ Yes | ✅ Yes |
| `/transactions` | ✅ Yes | ✅ Yes |
| `/users` | ❌ **NO** | ✅ **YES ONLY** |
| `/api/transactions/**` | ✅ Yes | ✅ Yes |
| `/api/users/**` | ❌ **NO** | ✅ **YES ONLY** |

## 🚀 Navigation Links

### Dashboard Navigation

The dashboard now includes **quick access buttons**:

1. **"Manage Transactions"** button
   - Visible to: ALL authenticated users
   - Links to: `/transactions`
   - Access: USER and ADMIN

2. **"Manage Users (Admin)"** button
   - Visible to: ADMIN users ONLY
   - Links to: `/users`
   - Access: ADMIN ONLY
   - **Non-admin users will NOT see this button**

### Page Headers

All pages include navigation bar with:
- Dashboard link
- Transactions link  
- Users link (ADMIN only)
- User profile
- Logout button

## 🛡️ Security Implementation

### Frontend Security

**Dashboard (dashboard.html)**
```html
<!-- Users button only shown for ADMIN -->
<a href="/users" th:if="${isAdmin}" ...>Manage Users (Admin)</a>
```

**Navigation Bar (transactions.html, users.html)**
```html
<!-- Users link only shown for ADMIN -->
<a href="/users" th:if="${isAdmin}">Users</a>
```

### Backend Security

**SecurityConfig.java**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/users").hasRole("ADMIN")           // Page access
    .requestMatchers("/api/users/**").hasRole("ADMIN")    // API access
    .requestMatchers("/api/**").authenticated()           // Other APIs
    .anyRequest().authenticated()
)
```

**DashboardController.java**
```java
// Sets isAdmin flag for frontend conditional rendering
boolean isAdmin = authentication.getAuthorities().stream()
    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
model.addAttribute("isAdmin", isAdmin);
```

## 🧪 Testing Access Control

### Test as Regular User (user1)

1. Login with: `user1 / user123`
2. **Should see:**
   - ✅ Dashboard
   - ✅ "Manage Transactions" button
   - ✅ Transactions page access via navbar
   - ✅ Can perform all transaction CRUD operations
3. **Should NOT see:**
   - ❌ "Manage Users" button on dashboard
   - ❌ "Users" link in navbar
4. **If they try to access /users directly:**
   - ❌ Gets **403 Forbidden** error
   - Redirected or blocked by Spring Security

### Test as Admin User (admin)

1. Login with: `admin / admin123`
2. **Should see:**
   - ✅ Dashboard
   - ✅ "Manage Transactions" button
   - ✅ "Manage Users (Admin)" button (highlighted in yellow/orange)
   - ✅ All navigation links including "Users"
   - ✅ Full access to all features

## 📋 Default User Accounts

```
ADMIN Account:
- Username: admin
- Password: admin123
- Role: ADMIN
- Access: Full system access

USER Account:
- Username: user1
- Password: user123
- Role: USER
- Access: Limited (no user management)
```

## 🚨 What Happens When Non-Admin Tries to Access User Management?

### Scenario 1: Click Hidden Link (if they inspect HTML)
```
Request: GET /users
Response: 403 Forbidden
Message: "Access Denied - You don't have permission to access this page"
Action: Spring Security blocks the request
```

### Scenario 2: API Call to User Endpoints
```
Request: GET /api/users
Response: 403 Forbidden
Headers: Requires ROLE_ADMIN
Action: API blocked by Spring Security
```

### Scenario 3: Try to Create/Edit Users via API
```
Request: POST /api/users
Response: 403 Forbidden
Action: Operation blocked, no data modified
```

## 🔐 Security Features

1. **Role-Based Authorization**
   - Enforced at controller level
   - API endpoints protected
   - Page access restricted

2. **Frontend Hiding**
   - Non-admin users don't see admin links
   - Cleaner UI, less confusion
   - Conditional rendering with Thymeleaf

3. **Backend Enforcement**
   - Even if frontend bypassed, backend blocks access
   - 403 Forbidden for unauthorized access
   - Logs security violations

4. **Session Management**
   - Role persists throughout session
   - Logout clears all permissions
   - Re-authentication required

## 📝 Implementation Details

### Files Modified

1. **src/main/resources/templates/dashboard.html**
   - Added navigation buttons
   - Conditional rendering for admin button

2. **src/main/java/com/payan/demo/controller/DashboardController.java**
   - Added `isAdmin` flag to all views
   - Checks user authorities

3. **src/main/java/com/payan/demo/config/SecurityConfig.java**
   - Added `.hasRole("ADMIN")` for /users
   - Added `.hasRole("ADMIN")` for /api/users/**
   - Enforces backend security

## 🎯 Best Practices Implemented

✅ **Defense in Depth**
   - Frontend AND backend protection
   - Multiple layers of security

✅ **Principle of Least Privilege**
   - Users only see what they can access
   - Minimal permissions by default

✅ **Clear Visual Indicators**
   - Admin-only features clearly marked
   - Color-coded buttons (yellow/orange for admin)

✅ **Graceful Degradation**
   - Regular users see fully functional app
   - No broken links or errors

## 🔄 Future Enhancements

Potential security improvements:
- [ ] Custom 403 error page
- [ ] Audit logging for admin actions
- [ ] IP-based restrictions
- [ ] Two-factor authentication for admin
- [ ] Session timeout configuration
- [ ] Password complexity requirements
- [ ] Account lockout after failed attempts

## 📞 Troubleshooting

**Problem:** Regular user sees "Users" link
**Solution:** Check if `isAdmin` is being passed to the view correctly

**Problem:** Admin gets 403 on /users
**Solution:** Verify user has ROLE_ADMIN (not just "ADMIN")

**Problem:** API calls fail with 403
**Solution:** Check SecurityConfig role requirements match user roles

---

**Version:** 1.0.0  
**Last Updated:** November 10, 2025  
**Status:** Production Ready ✅
