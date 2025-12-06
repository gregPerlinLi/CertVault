-- Initialize CertVault database
-- This script will be executed automatically when the PostgreSQL container starts

\c certvault;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE certvault TO certvault;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO certvault;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO certvault;

-- Add any initial schema or data here if needed