# MateAtesti Backend API

Node.js/Express REST API backend for MateAtesti application with MySQL database.

## Architecture

The application follows a clean layered architecture:

- **Controllers** - Handle HTTP requests and responses
- **Services** - Contain business logic
- **Models** - Define database schema using Sequelize ORM
- **Migrations** - Database schema versioning
- **Routes** - API endpoint definitions

## Installation

```bash
npm install
```

## Database Setup

### Prerequisites
- MySQL server running locally (default: localhost:3306)
- Database user credentials

### Configuration

1. Update `.env` file with your MySQL credentials:

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=mateattesti_db
DB_PORT=3306
NODE_ENV=development
PORT=3000
JWT_SECRET=replace-with-strong-secret
```

2. Create the database:
```bash
mysql -u root -p -e "CREATE DATABASE mateattesti_db;"
```

3. Run migrations:
```bash
npm run migrate
```

## Running the server

**Development mode (with auto-reload):**
```bash
npm run dev
```

**Production mode:**
```bash
npm start
```

Server runs on `http://localhost:3000`

## Folder Structure

```
├── config/
│   └── database.js          # Database configuration
├── migrations/              # Database migrations
│   ├── *-create-narucitelj.js
│   ├── *-create-radni-nalog.js
│   ├── *-create-document.js
│   └── *-create-note.js
├── models/                  # Sequelize models
│   ├── index.js
│   ├── narucitelj.js
│   ├── radni_nalog.js
│   ├── document.js
│   └── note.js
├── services/                # Business logic layer
│   ├── naruciteljiService.js
│   ├── radniNaloziService.js
│   ├── documentsService.js
│   └── notesService.js
├── controllers/             # Request handlers
│   ├── naruciteljiController.js
│   ├── radniNaloziController.js
│   ├── documentsController.js
│   └── notesController.js
├── routes/                  # API routes
│   ├── naruciteljRoutes.js
│   ├── radniNaloziRoutes.js
│   └── naruciteljiRadniNaloziRoutes.js
├── .env                     # Environment variables
├── .sequelizerc              # Sequelize CLI configuration
├── server.js                # Main application file
└── package.json
```

## API Endpoints

### Narucitelji (Clients)

- `GET /api/narucitelji` - Get clients with paging/sorting/filtering
  - Query params:
    - `page` (default `1`)
    - `pageSize` (default `10`, max `100`)
    - `sortBy` (`id|name|mjesto|drzava|postanskiBroj|OIB|email|createdAt|updatedAt`, default `name`)
    - `sortOrder` (`ASC|DESC`, default `ASC`)
    - `q` or `search` or `narucitelj` for global search
    - Field filters: `name`, `mjesto`, `drzava`, `postanskiBroj`, `OIB`, `email`
  - Response shape: `{ items, page, pageSize, totalItems, totalPages, sortBy, sortOrder }`
- `GET /api/narucitelji/:id` - Get client by ID
- `POST /api/narucitelji` - Create new client
  - Body: `{ name, location, adresa?, mjesto?, postanskiBroj?, drzava?, OIB?, ziroRacun?, ostalo?, kontaktOsoba?, telefon?, mobitel?, fax?, email?, comment? }`
- `PUT /api/narucitelji/:id` - Update client
  - Body may contain any of the fields above (`name?`, `location?`, etc.)
- `DELETE /api/narucitelji/:id` - Delete client

### Authentication

- `POST /api/auth/register` - Create new user
  - Body: `{ username, password }`
- `POST /api/auth/login` - Authenticate user
  - Body: `{ username, password }`
  - Response: `{ user, token }`

All non-auth routes require this header:

`Authorization: Bearer <token>`

### Radni Nalozi (Work Orders)

- `GET /api/radni-nalozi` - Get all work orders
- `GET /api/radni-nalozi/:id` - Get work order by ID
- `GET /api/radni-nalozi/:id/pdf` - Download generated PDF for a work order
- `GET /api/radni-nalozi/rn001.pdf` - Download generated `RN001.pdf`
- `POST /api/radni-nalozi` - Create new work order (narucitelj_id must reference an existing client)
  - Body: `{ narucitelj_id, datum, objekt, opis, fakturirano?, zavrseno?, pdfUrl?, brojPonude?, brojRacuna?, narudzbenica?, ugovor?, aktivnosti }`
  - `aktivnosti` is required and must be an array of aktivnost IDs (numbers)
- `PUT /api/radni-nalozi/:id` - Update work order (any provided narucitelj_id must reference an existing client)
  - Body: `{ narucitelj_id?, datum?, objekt?, opis?, fakturirano?, zavrseno?, pdfUrl?, brojPonude?, brojRacuna?, narudzbenica?, ugovor?, aktivnosti }`
  - `aktivnosti` is required and must be an array of aktivnost IDs (numbers); active aktivnosti are replaced from this list only
- `DELETE /api/radni-nalozi/:id` - Delete work order
- `GET /api/narucitelji/:naruciteljiId/radni-nalozi` - Get work orders by client

### Aktivnosti

- `GET /aktivnosti` - Get all active aktivnosti
- `GET /api/aktivnosti` - Same endpoint (compatibility alias)
- `GET /aktivnosti/:id` - Get aktivnost by ID (active only)
- `POST /aktivnosti` - Create new aktivnost
  - Body: `{ aktivnost, rokTrajanja }`
- `PUT /aktivnosti/:id` - Update aktivnost
  - Body: `{ aktivnost?, rokTrajanja? }`
- `DELETE /aktivnosti/:id` - Soft delete aktivnost (`isActive` set to `false`)

`aktivnosti` enum values:
- `ZastitaNaRadu`
- `ZastitaOdPozara`
- `ZastitaOkolisa`
- `OstalaMjerenja`

### Documents

- `GET /api/radni-nalozi/:radniNalogId/documents` - Get documents for work order
- `POST /api/radni-nalozi/:radniNalogId/documents` - Add document to work order
  - Body: `{ name, url }`
- `DELETE /api/radni-nalozi/:radniNalogId/documents/:documentId` - Delete document

### Notes

- `GET /api/radni-nalozi/:id/notes` - Get notes for work order
- `POST /api/radni-nalozi/:id/notes` - Add note to work order
- `DELETE /api/radni-nalozi/:id/notes/:noteIndex` - Delete note

### Health

- `GET /api/health` - Health check endpoint

## Download button for RN001.pdf

Use this in your frontend page (token is JWT from login):

```html
<button id="download-rn001">Download RN001.pdf</button>
<script>
  document.getElementById('download-rn001').addEventListener('click', async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('/api/radni-nalozi/rn001.pdf', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    if (!response.ok) {
      alert('Download failed');
      return;
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'RN001.pdf';
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  });
</script>
```

## Request/Response Examples

### Create Client
```bash
POST /api/narucitelji
Content-Type: application/json

{
  "name": "Novi Klijent",
  "location": "Zagreb",
  "comment": "Komentar"
}
```

### Create Work Order
```bash
POST /api/radni-nalozi
Content-Type: application/json

{
  "narucitelj_id": 1,
  "datum": "2023-10-01",
  "objekt": "Objekt A",
  "fakturirano": true,
  "zavrseno": false,
  "opis": "Opis nalog",
  "brojPonude": "P-123",
  "brojRacuna": "R-123",
  "narudzbenica": "NAR-123",
  "ugovor": "UG-123",
  "aktivnosti": [1, 2]
}
```

### Add Note
```bash
POST /api/radni-nalozi/RN001/notes
Content-Type: application/json

{
  "date": "2023-10-01",
  "text": "Nova napomena"
}
```
