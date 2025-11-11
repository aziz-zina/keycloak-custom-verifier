# Keycloak Hospital Code Verifier

> A custom Keycloak Required Action provider for hospital code verification and doctor assignment

## 📋 Overview

This Keycloak extension implements a custom authentication flow that:

- ✅ **Validates hospital codes** during user login
- 🏥 **Assigns doctors to hospitals** via external API integration
- 🔐 **Enforces password updates** after successful verification
- 📝 **Provides custom login forms** for seamless user experience

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Keycloak 26.4.2
- Access to backend API for hospital assignment

### Build

```bash
mvn clean package
```

The compiled JAR will be available at `target/verifier-1.0-SNAPSHOT.jar`

### Installation

1. **Build the extension**

   ```bash
   mvn clean package
   ```

2. **Deploy to Keycloak**

   ```bash
   cp target/verifier-1.0-SNAPSHOT.jar $KEYCLOAK_HOME/providers/
   ```

3. **Restart Keycloak**

   ```bash
   $KEYCLOAK_HOME/bin/kc.sh build
   $KEYCLOAK_HOME/bin/kc.sh start
   ```

## ⚙️ Configuration

### Environment Variables

Configure the following environment variables in your Keycloak deployment:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `CATH_BACKEND_BASE_URL` | Base URL for the backend API | `http://localhost:8085` | No |
| `PATH_VERIFY_API` | API path for verification endpoint | - | Yes |

**Example:**

```bash
export CATH_BACKEND_BASE_URL="https://api.example.com"
export PATH_VERIFY_API="/api/v1/doctors"
```

### User Setup

Each user must have the following attribute configured in Keycloak:

- **Attribute Name:** `hospital_code`
- **Value:** The unique hospital verification code

**Setting via Admin Console:**

1. Navigate to Users → Select User → Attributes
2. Add attribute: `hospital_code` with the appropriate value
3. Click "Save"

## 📖 Usage

### Enabling the Required Action

**Via Admin Console:**

1. Go to **Authentication** → **Required Actions**
2. Enable "Custom Password Flow with Code"
3. Assign to users as needed

**Via API:**

```bash
# Add required action to a user
curl -X PUT "http://localhost:8080/admin/realms/{realm}/users/{user-id}/required-actions/verify-hospital-code" \
  -H "Authorization: Bearer {access-token}"
```

### User Experience Flow

1. User logs in with username/password
2. System prompts for hospital code entry
3. Code is validated against user's `hospital_code` attribute
4. On success:
   - Doctor is assigned to hospital via API call
   - User is prompted to update password
   - Required action is removed
5. On failure:
   - Error message is displayed
   - User can retry

## 🏗️ Architecture

### Project Structure

```
code_verifier/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/keycloak/verifier/
    │   │   ├── VerifyHospitalCodeActionProvider.java    # Main logic
    │   │   ├── VerifyHospitalCodeActionFactory.java     # Provider factory
    │   │   └── RestClient.java                          # API integration
    │   └── resources/
    │       └── META-INF/services/
    │           └── org.keycloak.authentication.RequiredActionFactory
    └── test/
        └── java/
```

### Components

| Component | Description |
|-----------|-------------|
| `VerifyHospitalCodeActionProvider` | Handles the verification logic and form processing |
| `VerifyHospitalCodeActionFactory` | Factory for creating provider instances |
| `RestClient` | HTTP client for backend API communication |

### API Integration

The extension calls the backend API endpoint:

```http
PUT {CATH_BACKEND_BASE_URL}{PATH_VERIFY_API}/assign-to-hospital?doctorId={uuid}
```

**Expected Response:**

- `2xx` - Success, doctor assigned
- Other - Failure, shows error to user

## 🔍 Troubleshooting

### Common Issues

**Issue:** Provider not showing in Keycloak

- **Solution:** Ensure JAR is in `providers/` directory and Keycloak has been rebuilt with `kc.sh build`

**Issue:** API call fails

- **Solution:** Verify environment variables are set correctly and backend API is accessible

**Issue:** Invalid hospital code error

- **Solution:** Check that user has `hospital_code` attribute set in Keycloak

### Logs

Enable debug logging to troubleshoot:

```bash
# Add to keycloak.conf or set environment variable
KC_LOG_LEVEL=DEBUG,com.keycloak.verifier:TRACE
```

## 🛠️ Development

### Build for Development

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

### Code Style

- Java 21 features supported
- Follow Keycloak SPI conventions
- Ensure proper error handling and logging

## 📝 License

This project is proprietary software. All rights reserved.

---

**Maintained by:** Aziz Zina  
**Last Updated:** November 2025
