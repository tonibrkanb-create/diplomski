# Architecture Refactoring Guide

## Overview

Your application has been refactored from a monolithic server with mock data to a professional layered architecture with MySQL database integration. This guide explains the new structure and how to use it.

## New Directory Structure

```
MateAtestiBe/
├── config/
│   └── database.js              # Database configuration
├── controllers/                 # HTTP request handlers
│   ├── naruciteljiController.js
│   ├── radniNaloziController.js
│   ├── documentsController.js
│   └── notesController.js
├── models/                      # Sequelize ORM models
│   ├── index.js
│   ├── narucitelj.js
│   ├── radni_nalog.js
│   ├── document.js
│   └── note.js
├── migrations/                  # Database schema migrations
│   ├── 20260226120000-create-narucitelj.js
│   ├── 20260226120001-create-radni-nalog.js
│   ├── 20260226120002-create-document.js
│   └── 20260226120003-create-note.js
├── services/                    # Business logic layer
│   ├── naruciteljiService.js
│   ├── radniNaloziService.js
│   ├── documentsService.js
│   └── notesService.js
├── routes/                      # API endpoint definitions
│   ├── naruciteljRoutes.js
│   ├── radniNaloziRoutes.js
│   └── naruciteljiRadniNaloziRoutes.js
├── seeders/                     # Initial database data
│   └── 20260226120000-initial-data.js
├── .env                         # Environment variables
├── .sequelizerc                 # Sequelize configuration
├── server.js                    # Main application entry point
└── package.json
```

## Architecture Layers

### 1. **Controllers** (`/controllers`)
- Handle incoming HTTP requests
- Call appropriate service methods
- Return JSON responses
- Handle error responses

### 2. **Services** (`/services`)
- Contain all business logic
- Interact with models
- Validation and data transformation
- Reusable across multiple controllers

### 3. **Models** (`/models`)
- Define database schema using Sequelize
- Handle data associations
- Type definitions and constraints

### 4. **Routes** (`/routes`)
- Define API endpoints
- Map HTTP methods to controller actions
- Route organization by resource

## Setup Instructions

### Prerequisites
- Node.js installed
- MySQL server running
- Basic understanding of REST APIs

### Step 1: Install Dependencies

```bash
npm install
```

This will install:
- `express` - Web framework
- `sequelize` - ORM for MySQL
- `mysql2` - MySQL driver
- `dotenv` - Environment variables
- `cors` - Cross-origin resource sharing
- `body-parser` - Request body parsing

### Step 2: Configure Database

1. Edit `.env` file with your MySQL credentials:

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=mateattesti_db
DB_PORT=3306
NODE_ENV=development
PORT=3000
```

2. Create the database (if not exists):

```bash
mysql -u root -p -e "CREATE DATABASE mateattesti_db;"
```

### Step 3: Run Migrations

Migrations create the database tables automatically:

```bash
npm run migrate
```

This executes migrations in order:
1. Creates `narucitelji` table
2. Creates `radni_nalozi` table
3. Creates `documents` table
4. Creates `notes` table

### Step 4: (Optional) Seed Initial Data

Load sample data from the seeder:

```bash
sequelize-cli db:seed:all
```

This populates the database with test data matching your previous mock data.

### Step 5: Start the Server

Development mode with auto-reload:
```bash
npm run dev
```

Production mode:
```bash
npm start
```

Server will run on `http://localhost:3000`

## Database Schema

### narucitelj
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- name (STRING, NOT NULL)
- adresa (STRING)
- mjesto (STRING)
- postanskiBroj (STRING)
- drzava (STRING)
- OIB (STRING)
- ziroRacun (STRING)
- ostalo (TEXT)
- kontaktOsoba (STRING)
- telefon (STRING)
- mobitel (STRING)
- fax (STRING)
- email (STRING)
- location (STRING, NOT NULL)
- comment (TEXT)
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

### user
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- username (STRING, UNIQUE, NOT NULL)
- password (STRING, NOT NULL)  -- hashed using bcrypt
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

### radni_nalog
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- brojNaloga (STRING, UNIQUE, NOT NULL)
- narucitelj_id (INTEGER, FOREIGN KEY)
- datum (DATE, NOT NULL)
- objekt (STRING, NOT NULL)
- fakturirano (BOOLEAN, DEFAULT FALSE)
- zavrseno (BOOLEAN, DEFAULT FALSE)
- opis (TEXT)
- brojPonude (STRING)
- brojRacuna (STRING)
- narudzbenica (STRING)
- ugovor (STRING)
- aktivnosti (ENUM: ZastitaNaRadu, ZastitaOdPozara, ZastitaOkolisa, OstalaMjerenja)
- pdfUrl (STRING)
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

### aktivnost
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- aktivnost (ENUM: ZastitaNaRadu, ZastitaOdPozara, ZastitaOkolisa, OstalaMjerenja)
- rokTrajanja (INTEGER)
- isActive (BOOLEAN, DEFAULT TRUE)
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

### document
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- name (STRING, NOT NULL)
- url (STRING, NOT NULL)
- radni_nalog_id (INTEGER, FOREIGN KEY)
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

### note
```sql
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- date (DATE, NOT NULL)
- text (TEXT, NOT NULL)
- radni_nalog_id (INTEGER, FOREIGN KEY)
- createdAt (DATETIME)
- updatedAt (DATETIME)
```

## Migration Commands

### Run migrations
```bash
npm run migrate
```

### Undo all migrations
```bash
npm run migrate:undo
```

### Seed data
```bash
sequelize-cli db:seed:all
```

## API Usage Examples

### Get all narucitelji
```bash
curl "http://localhost:3000/api/narucitelji?q=Narucitelj"
# or using the field name
curl "http://localhost:3000/api/narucitelji?narucitelj=Narucitelj"
```

### Create new narucitelj
```bash
curl -X POST http://localhost:3000/api/narucitelji \
  -H "Content-Type: application/json" \
  -d '{
    "narucitelj": "New Client",
    "location": "Varazdin",
    "adresa": "Ulica 123",
    "mjesto": "Varazdin",
    "postanskiBroj": "42000",
    "drzava": "Hrvatska",
    "OIB": "98765432109",
    "ziroRacun": "HR1210010051863000160",
    "kontaktOsoba": "Pero",
    "telefon": "042 123 456",
    "email": "pero@example.com",
    "comment": "Important client"
  }'
```

### Get work orders by narucitelj
```bash
curl http://localhost:3000/api/narucitelji/1/radni-nalozi
```

### Register a new user
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Login user
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```
### Create new work order
```bash
curl -X POST http://localhost:3000/api/radni-nalozi \
  -H "Content-Type: application/json" \
  -d '{
    "narucitelj_id": 1,
    "datum": "2024-02-26",
    "objekt": "New Building",
    "opis": "Installation work",
    "fakturirano": false,
    "zavrseno": false,
    "brojPonude": "P-006",
    "brojRacuna": "R-006",
    "narudzbenica": "NAR-006",
    "ugovor": "UG-006",
    "aktivnosti": "ZastitaNaRadu",
    "pdfUrl": "/pdfs/RN006/plan.pdf"
  }'
```

### Add document to work order
```bash
curl -X POST http://localhost:3000/api/radni-nalozi/1/documents \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Invoice.pdf",
    "url": "/docs/invoice.pdf"
  }'
```

## Benefits of This Architecture

1. **Separation of Concerns** - Each layer has a single responsibility
2. **Maintainability** - Easy to locate and modify code
3. **Testability** - Services can be tested independently
4. **Scalability** - Easy to add new features without affecting other parts
5. **Reusability** - Services can be used by different controllers
6. **Database Management** - Migrations provide version control for schema changes
7. **Type Safety** - Clear structure with defined interfaces through Sequelize

## Common Tasks

### Adding a New Endpoint

1. Create or update service in `/services`
2. Create or update controller in `/controllers`
3. Add route in `/routes`
4. Import and use in `server.js`

### Creating a New Table

1. Create migration file in `/migrations`
2. Create model in `/models`
3. Add associations if needed
4. Run `npm run migrate`

### Modifying Database Schema

1. Create new migration file
2. Add changes and rollback logic
3. Run `npm run migrate`

## Troubleshooting

### Database connection fails
- Check `.env` file credentials
- Verify MySQL server is running
- Ensure database exists

### Migration errors
- Delete tables manually and run migrate again
- Check migration file syntax
- Verify foreign key constraints

### Port already in use
- Change `PORT` in `.env` file
- Or kill process using the port

## Next Steps

1. Test all API endpoints
2. Update frontend to use new database instead of mock data
3. Add validation middleware
4. Implement authentication/authorization
5. Add error handling middleware
6. Create integration tests
7. Set up production database

## Support

For Sequelize documentation: https://sequelize.org/
For Express documentation: https://expressjs.com/
