require('dotenv').config();

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const db = require('./models');

// Import routes
const naruciteljRoutes = require('./routes/naruciteljRoutes');
const radniNaloziRoutes = require('./routes/radniNaloziRoutes');
const naruciteljiRadniNaloziRoutes = require('./routes/naruciteljiRadniNaloziRoutes');
const authRoutes = require('./routes/authRoutes');
const aktivnostiRoutes = require('./routes/aktivnostiRoutes');
const korisnikRoutes = require('./routes/korisnikRoutes');
const userManagementRoutes = require('./routes/userManagementRoutes');
const managementRoutes = require('./routes/managementRoutes');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


// Routes
app.use('/api/auth', authRoutes);

app.use('/api/narucitelji', naruciteljRoutes);
app.use('/api/radni-nalozi', radniNaloziRoutes);
app.use('/api/narucitelji', naruciteljiRadniNaloziRoutes);
app.use('/api/aktivnosti', aktivnostiRoutes);
app.use('/aktivnosti', aktivnostiRoutes);
app.use('/api/korisnik', korisnikRoutes);
app.use('/api/users', userManagementRoutes);
app.use('/api/management', managementRoutes);

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
