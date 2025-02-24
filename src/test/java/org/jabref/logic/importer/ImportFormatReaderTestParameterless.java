package org.jabref.logic.importer;

import org.jabref.logic.xmp.XmpPreferences;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.model.util.FileUpdateMonitor;
import org.jabref.preferences.GeneralPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImportFormatReaderTestParameterless {

    private ImportFormatReader reader;
    private final FileUpdateMonitor fileMonitor = new DummyFileUpdateMonitor();

    @BeforeEach
    void setUp() {
        reader = new ImportFormatReader();
        GeneralPreferences generalPreferences = mock(GeneralPreferences.class, Answers.RETURNS_DEEP_STUBS);
        when(generalPreferences.getDefaultEncoding()).thenReturn(StandardCharsets.UTF_8);
        ImportFormatPreferences importFormatPreferences = mock(ImportFormatPreferences.class, Answers.RETURNS_DEEP_STUBS);
        when(importFormatPreferences.getCustomImportList()).thenReturn(Set.of());
        reader.resetImportFormats(mock(ImporterPreferences.class), generalPreferences, importFormatPreferences, mock(XmpPreferences.class), fileMonitor);
    }

    @Test
    void importUnknownFormatThrowsExceptionIfNoMatchingImporterWasFound() throws Exception {
        Path file = Path.of(ImportFormatReaderTestParameterless.class.getResource("fileformat/emptyFile.xml").toURI());
        assertThrows(ImportException.class, () -> reader.importUnknownFormat(file, fileMonitor));
    }

    @Test
    void importUnknownFormatThrowsExceptionIfPathIsNull() throws Exception {
        assertThrows(NullPointerException.class, () -> reader.importUnknownFormat(null, fileMonitor));
    }

    @Test
    void importUnknownFormatThrowsExceptionIfDataIsNull() throws Exception {
        assertThrows(NullPointerException.class, () -> reader.importUnknownFormat(null));
    }

    @Test
    void importFromFileWithUnknownFormatThrowsException() throws Exception {
        assertThrows(ImportException.class, () -> reader.importFromFile("someunknownformat", Path.of("somepath")));
    }
    
    //test added for group-34 issue #8 Check for missing commas in .bib file (Github #8011)
    @Test
    void importBibFileThrowsExceptionIfTheFileWithFieldsThatAreNotSeperatedByComma() throws Exception {
        Path file = Path.of(ImportFormatReaderTestParameterless.class.getResource("fileformat/BibWithMissingComma.bib").toURI());
        Exception exception = assertThrows(ImportException.class, () -> reader.importUnknownFormat(file, fileMonitor));
        assertEquals("Error occurred when parsing entry: 'Error in line 8 or above: Empty text token.\n" +
                "This could be caused by a missing comma between two fields.'. Skipped entry.", exception.getMessage());
    }
}
