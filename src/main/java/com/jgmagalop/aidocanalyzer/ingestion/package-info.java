/**
 * Procesamiento asíncrono de documentos cargados: extracción de texto,
 * fragmentación (chunking), metadata, generación de embeddings y almacenamiento
 * en el vector store. Actualiza el estado del documento a INDEXED o FAILED.
 */
package com.jgmagalop.aidocanalyzer.ingestion;