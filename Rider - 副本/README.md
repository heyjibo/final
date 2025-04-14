# Campus Food Delivery Rider System (Simplified Version)

This is a simplified version of a campus food delivery rider system developed with Vue2 framework, providing the most basic functionality.

## Features

- Login/Registration system
- Rider dashboard homepage
- View orders and basic statistics
- Order acceptance functionality

## Technology Stack

- Vue 2.7.7
- Vue Router 3.6.5
- Element UI 2.15.14
- Axios 1.8.4
- Vite (build tool)

## How to Run

```bash
# Install dependencies
npm install

# Run in development mode
npm run dev

# Build production version
npm run build
```

## Default Accounts

The system includes the following default accounts for testing:

- Rider account: username `rider`, password `rider123`
- Admin account: username `admin`, password `admin123`

## Project Structure

```
├── src/
│   ├── api/               # API requests and mock data
│   ├── views/             # Page components
│   │   ├── auth/          # Authentication related pages
│   │   └── rider/         # Rider related pages
│   ├── router/            # Route configuration
│   ├── App.vue            # Root component
│   └── main.js            # Entry file
├── public/                # Static resources
└── index.html             # HTML template
```

## Next Steps

1. Add more rider features, such as order details and delivery routes
2. Improve user information management
3. Add real-time notification system
4. Optimize mobile experience
