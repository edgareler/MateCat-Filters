package com.matecat.converter.core;

import org.junit.Test;

import java.io.File;
import java.util.Locale;

import static org.junit.Assert.*;

public class XliffGeneratorTest {


    /**
     * As every sub component is tested, the only purpose of this test is to check that
     * no exception is thrown
     * @throws Exception
     */
    @Test
    public void testGenerate() throws Exception {

        File file = new File(getClass().getResource("/generation/test.docx").getPath());
        XliffGenerator generator = new XliffGenerator(Locale.ENGLISH, Locale.ENGLISH, file, null);
        File xlf = generator.generate();
        assertNotNull(xlf);
        assertTrue(xlf.exists());
        assertFalse(xlf.isDirectory());

    }
}