INSERT INTO users (email, password_hash, first_name, last_name, role, department, active, tokens_valid_since, created_at)
VALUES
    ('admin@aidoc.dev',
     '$2a$10$1fNKfOIhGZkXn3Sk6HUpveafZeA3c05gqeFYLsEoEpmC/pvz/Pl.G',
     'Ana', 'Torres',
     'ADMIN', 'IT', TRUE, NOW(), NOW()),

    ('empleado.rh@aidoc.dev',
     '$2a$10$1fNKfOIhGZkXn3Sk6HUpveafZeA3c05gqeFYLsEoEpmC/pvz/Pl.G',
     'Luis', 'Ramírez',
     'EMPLOYEE', 'HR', TRUE, NOW(), NOW()),

    ('empleado.finanzas@aidoc.dev',
     '$2a$10$1fNKfOIhGZkXn3Sk6HUpveafZeA3c05gqeFYLsEoEpmC/pvz/Pl.G',
     'María', 'López',
     'EMPLOYEE', 'FINANCE', TRUE, NOW(), NOW());