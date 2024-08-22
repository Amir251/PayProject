
# Wallet-Demo

**Wallet-Demo** is a simple wallet management application that allows users to register, log in, and manage their digital wallets. The application supports user authentication through JWT, role-based access control, and provides several wallet-related services.

## Features

- **User Registration and Login:** Register new users and allow them to log in.
- **Password Encryption:** User passwords are securely stored as hashed values using Bcrypt.
- **JWT Authentication:** Secure the application using JWT tokens.
- **Role-Based Access Control:** Restrict method-level access based on user roles.
- **View Balance and Transaction History:** Users can view their wallet balance and transaction history.
- **Money Transfer:** Transfer money between user accounts.
- **Wallet Top-up:** Admins can top-up user wallets.
- **Product Management:** Admins can define products, and users can purchase them.
- **Email Notifications:** Emails are sent to users for account activation and transaction updates.

## Installation

To run the project, you need:

- **JDK 17**
- **Maven**
- **Oracle Database** (or a similar compatible database)

You can access the project by either:

1. **Running the project directly** on your local machine using Maven and JDK 17.
2. **Using Docker** with the following command:
   ```bash
   docker pull ghcr.io/amir251/payproject:latest
   docker run --env-file <path_to_env> -p 8081:8081 ghcr.io/amir251/payproject:latest
   ```

   Make sure to replace `<path_to_env>` with the path to your `.env` file.

## Usage

After running the project, you can access the API documentation and explore the available endpoints through Swagger at:

[http://localhost:8081/swagger-ui/index.html#/](http://localhost:8081/swagger-ui/index.html#/)

## .env Configuration

Ensure that your `.env` file is correctly set up with the following configurations:

```plaintext
# Database Configuration
DB_URL=add:port:xe
DB_USERNAME=xxxx
DB_PASSWORD=xxxx

# Mail Server Configuration
MAIL_HOST=email.provider
MAIL_PORT=xxx
MAIL_USERNAME=xxx
MAIL_PASSWORD=xxx

# JWT Configuration
JWT_SECRET=xxxxxxx

# Admin info
USER_FIRST_NAME=xx
USER_LAST_NAME=xx
USER_EMAIL=xx
USER_PHONE=xx
USER_PASSWD=xx
```

## Support

For any questions or issues, please contact:

- **Email:** [amir.shahravi@yahoo.com](mailto:amir.shahravi@yahoo.com)
