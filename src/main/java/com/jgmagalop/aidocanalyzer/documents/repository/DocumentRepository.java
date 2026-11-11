package com.jgmagalop.aidocanalyzer.documents.repository;

import com.jgmagalop.aidocanalyzer.documents.domain.Document;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentCategory;
import com.jgmagalop.aidocanalyzer.documents.domain.DocumentStatus;
import com.jgmagalop.aidocanalyzer.security.user.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findBySha256(String sha256);

    /**
     * Busca documentos aplicando filtros opcionales y la regla de permisos.
     * <ul>
     *   <li>category / status: si son null, no se filtra por ellos.</li>
     *   <li>allDepartments = true (ADMIN): devuelve documentos de todos los departamentos.</li>
     *   <li>allDepartments = false: solo los del departamento indicado y los generales (department null).</li>
     * </ul>
     * Ordenados del más reciente al más antiguo.
     */
    @Query("""
        SELECT d FROM Document d
        WHERE (:category IS NULL OR d.category = :category)
          AND (:status IS NULL OR d.status = :status)
          AND (:allDepartments = TRUE OR d.department IS NULL OR d.department = :department)
        """)
    Page<Document> search(@Param("category") DocumentCategory category,
                          @Param("status") DocumentStatus status,
                          @Param("department") Department department,
                          @Param("allDepartments") boolean allDepartments,
                          Pageable  pageable); //Pageable indica el paginado

}