import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    public void testSaveNewDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Extender")
                .content("New America")
                .author(DocumentManager.Author.builder().id("1").name("Jonny").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Extender", savedDocument.getTitle());
        assertEquals("New America", savedDocument.getContent());
    }

    @Test
    public void testSaveExistingDocument() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(documentId)
                .title("Hostel")
                .content("USA")
                .author(DocumentManager.Author.builder().id("1").name("Jonny").build())
                .created(Instant.now())
                .build();

        documentManager.save(document);

        DocumentManager.Document updatedDocument = DocumentManager.Document.builder()
                .id(documentId)
                .title("Updated Hostel")
                .content("England")
                .author(DocumentManager.Author.builder().id("1").name("Lester").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(updatedDocument);

        assertEquals(documentId, savedDocument.getId());
        assertEquals("Updated Hostel", savedDocument.getTitle());
        assertEquals("England", savedDocument.getContent());
    }

    @Test
    public void testFindById() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(documentId)
                .title("Boozy")
                .content("Island")
                .author(DocumentManager.Author.builder().id("1").name("Corland").build())
                .created(Instant.now())
                .build();

        documentManager.save(document);

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(documentId);

        assertTrue(foundDocument.isPresent());
        assertEquals(documentId, foundDocument.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("non-existent-id");

        assertFalse(foundDocument.isPresent());
    }

    @Test
    public void testSearchByTitlePrefix() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("PrefixTitle1")
                .content("Content1")
                .author(DocumentManager.Author.builder().id("1").name("Author1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("PrefixTitle2")
                .content("Content2")
                .author(DocumentManager.Author.builder().id("2").name("Author2").build())
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Arrays.asList("Prefix"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);

        assertEquals(2, result.size());
    }

    @Test
    public void testSearchByContent() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Title1")
                .content("UniqueContent")
                .author(DocumentManager.Author.builder().id("1").name("Author1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Title2")
                .content("Content2")
                .author(DocumentManager.Author.builder().id("2").name("Author2").build())
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(Arrays.asList("UniqueContent"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);

        assertEquals(1, result.size());
        assertEquals("Title1", result.get(0).getTitle());
    }
}