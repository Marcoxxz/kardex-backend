-- =====================================================================
-- 1. POBLAR TABLA ESTUDIANTES
-- =====================================================================
INSERT INTO estudiantes (ru, ci, nombres, apellidos, carrera) 
SELECT 'RU-8821', '1234567', 'Juan Carlos', 'Pérez Choque', 'Ingeniería de Sistemas'
WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE ru = 'RU-8821');

INSERT INTO estudiantes (ru, ci, nombres, apellidos, carrera) 
SELECT 'RU-4512', '7654321', 'María René', 'Fernández Flores', 'Ingeniería de Sistemas'
WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE ru = 'RU-4512');

INSERT INTO estudiantes (ru, ci, nombres, apellidos, carrera) 
SELECT 'RU-9931', '4567890', 'Alex', 'Mamani Quispe', 'Ingeniería Informática'
WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE ru = 'RU-9931');

INSERT INTO estudiantes (ru, ci, nombres, apellidos, carrera) 
SELECT 'RU-1102', '9876543', 'Ana Sofía', 'Vargas Beltrán', 'Ingeniería de Sistemas'
WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE ru = 'RU-1102');


-- =====================================================================
-- 2. POBLAR TABLA USUARIOS (LOGIN CON HASHES MD5 DÉBILES)
-- =====================================================================
INSERT INTO usuarios (username, password, nombre_real, rol) 
SELECT 'admin', '0192023a7bbd73250516f069df18b500', 'Director de Carrera UATF', 'ADMINISTRADOR'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO usuarios (username, password, nombre_real, rol) 
SELECT 'docente_perez', 'ac99fecf6fcb8c25d18788d14a5384ee', 'Ing. Alexander Pérez', 'DOCENTE'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'docente_perez');


-- =====================================================================
-- 3. POBLAR TABLA NOTAS (ENTORNO VULNERABLE A IDOR)
-- =====================================================================
-- Notas del Estudiante 1 (Juan Carlos - RU-8821)
INSERT INTO notas (ru, sigla_materia, nombre_materia, nota_final, gestion) 
SELECT 'RU-8821', 'SIS-412', 'Seguridad de la Información', 85, '1/2026'
WHERE NOT EXISTS (SELECT 1 FROM notas WHERE ru = 'RU-8821' AND sigla_materia = 'SIS-412');

INSERT INTO notas (ru, sigla_materia, nombre_materia, nota_final, gestion) 
SELECT 'RU-8821', 'SIS-321', 'Sistemas Operativos I', 71, '2/2025'
WHERE NOT EXISTS (SELECT 1 FROM notas WHERE ru = 'RU-8821' AND sigla_materia = 'SIS-321');

-- Notas de la Estudiante 2 (María René - RU-4512)
INSERT INTO notas (ru, sigla_materia, nombre_materia, nota_final, gestion) 
SELECT 'RU-4512', 'SIS-412', 'Seguridad de la Información', 100, '1/2026'
WHERE NOT EXISTS (SELECT 1 FROM notas WHERE ru = 'RU-4512' AND sigla_materia = 'SIS-412');

INSERT INTO notas (ru, sigla_materia, nombre_materia, nota_final, gestion) 
SELECT 'RU-4512', 'SIS-222', 'Base de Datos II', 51, '2/2025'
WHERE NOT EXISTS (SELECT 1 FROM notas WHERE ru = 'RU-4512' AND sigla_materia = 'SIS-222');


-- =====================================================================
-- 4. POBLAR TABLA RECLAMOS (ENTORNO VULNERABLE A XSS ALMACENADO)
-- =====================================================================
INSERT INTO reclamos (ru, asunto, detalle) 
SELECT 'RU-9931', 'Horarios de laboratorio', 'Buenas tardes, solicitamos que los laboratorios de cómputo se abran los sábados por la mañana para avanzar los proyectos.'
WHERE NOT EXISTS (SELECT 1 FROM reclamos WHERE ru = 'RU-9931' AND asunto = 'Horarios de laboratorio');

INSERT INTO reclamos (ru, asunto, detalle) 
SELECT 'RU-1102', 'Actualización de Kardex', 'Tengo problemas con la convalidación de mi matrícula de la gestión pasada, no aparece reflejada en el sistema.'
WHERE NOT EXISTS (SELECT 1 FROM reclamos WHERE ru = 'RU-1102' AND asunto = 'Actualización de Kardex');

-- =====================================================================
-- 5. datos materia
-- =====================================================================
-- Insertar datos de ejemplo
INSERT INTO materias (sigla, nombre, creditos, carrera, semestre, requisito, area) VALUES
('INF-101', 'Programación I', 4, 'Ing. Informática', 1, NULL, 'Programación'),
('INF-102', 'Programación II', 4, 'Ing. Informática', 2, 'INF-101', 'Programación'),
('INF-201', 'Bases de Datos I', 4, 'Ing. Informática', 3, NULL, 'Bases de Datos'),
('MAT-101', 'Cálculo I', 5, 'Ing. Informática', 1, NULL, 'Matemáticas'),
('MAT-102', 'Cálculo II', 5, 'Ing. Informática', 2, 'MAT-101', 'Matemáticas');