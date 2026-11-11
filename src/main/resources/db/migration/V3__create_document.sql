CREATE TABLE documents (
                           id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           title              VARCHAR(255) NOT NULL,
                           original_filename  VARCHAR(255) NOT NULL,
                           document_type      VARCHAR(10)  NOT NULL CHECK (document_type IN ('PDF', 'DOCX')),
                           category           VARCHAR(50)  NOT NULL CHECK (category IN ('MANUAL', 'CONTRACT', 'PROCEDURE', 'TECHNICAL')),
                           department         VARCHAR(50)           CHECK (department IN ('HR', 'FINANCE', 'IT', 'LEGAL', 'OPERATIONS')),
                           size_bytes         BIGINT    NOT NULL,
                           sha256             VARCHAR(64)     NOT NULL UNIQUE,
                           storage_path       VARCHAR(500) NOT NULL,
                           status             VARCHAR(20)  NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'INDEXED', 'FAILED')),
                           error_message      TEXT,
                           chunk_count        INTEGER,
                           uploaded_by_id     BIGINT       NOT NULL REFERENCES users (id),
                           created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
                           indexed_at         TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_documents_department ON documents (department);
CREATE INDEX idx_documents_status     ON documents (status);