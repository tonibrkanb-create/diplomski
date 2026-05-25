const jwt = require('jsonwebtoken');

module.exports = function korisnikAuthMiddleware(req, res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Authorization token is required' });
  }

  const token = authHeader.substring(7);

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'change-me-in-production');
    if (!decoded.korisnikId) {
      return res.status(401).json({ message: 'Invalid token type' });
    }
    req.korisnik = decoded;
    return next();
  } catch (error) {
    return res.status(401).json({ message: 'Invalid or expired token' });
  }
};
