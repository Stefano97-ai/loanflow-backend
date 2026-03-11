-- DATOS INICIALES - LoanFlow
INSERT INTO users (name, email, password, dni, phone, role, created_at)
VALUES
    ('Admin LoanFlow', 'admin@loanflow.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '00000001', '999000001', 'ADMIN', NOW()),
    ('Carlos Ramirez', 'carlos@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '12345678', '987654321', 'CLIENT', NOW()),
    ('Maria Torres', 'maria@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '87654321', '976543210', 'CLIENT', NOW())
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

INSERT INTO loan_types (name, description, min_amount, max_amount, min_months, max_months, annual_interest_rate, created_at)
VALUES
  ('Prestamo Personal', 'Para gastos personales o imprevistos', 500.00, 15000.00, 3, 36, 18.00, NOW()),
  ('Prestamo Vehicular', 'Financiamiento para vehiculos nuevos o usados', 5000.00, 80000.00, 12, 60, 12.00, NOW()),
  ('Prestamo Hipotecario', 'Para compra o construccion de vivienda', 50000.00, 500000.00, 60, 240, 9.50, NOW()),
  ('Prestamo Educativo', 'Financiamiento de estudios superiores', 1000.00, 30000.00, 6, 48, 10.00, NOW())
ON CONFLICT DO NOTHING;
