package com.bbtest.utils;

import com.bbtest.records.Task;
import org.junit.jupiter.api.Test;

import static com.bbtest.utils.Decoder.BASE64;
import static com.bbtest.utils.Decoder.ROT13;
import static org.junit.jupiter.api.Assertions.*;

public class DecoderTests {

    private final Task referenceTask = new Task(
            "hZejwDlx",
            "Gamble",
            1,
            20,
            "Investigate Hayim Summerfield and find out their relation to the magic horse.",
            null
    );

    @Test
    void decryptNotEncryptedTask() {
        Task decoded = Decoder.decryptIfPossible(referenceTask);
        assertEquals(referenceTask, decoded);
    }

    @Test
    void decryptUnknownEncryption() {
        Task encoded = new Task(
                "aFplandEbHg=",
                "R2FtYmxl",
                1,
                20,
                "SW52ZXN0aWdhdGUgSGF5aW0gU3VtbWVyZmllbGQgYW5kIGZpbmQgb3V0IHRoZWlyIHJlbGF0aW9uIHRvIHRoZSBtYWdpYyBob3JzZS4=",
                (byte) 3
        );
        Task decoded = Decoder.decryptIfPossible(encoded);
        assertEquals(encoded, decoded);
    }

    @Test
    void decryptBase64Task() {
        Task encoded = new Task(
                "aFplandEbHg=",
                "R2FtYmxl",
                1,
                20,
                "SW52ZXN0aWdhdGUgSGF5aW0gU3VtbWVyZmllbGQgYW5kIGZpbmQgb3V0IHRoZWlyIHJlbGF0aW9uIHRvIHRoZSBtYWdpYyBob3JzZS4=",
                BASE64
        );
        Task decoded = Decoder.decryptIfPossible(encoded);
        assertEquals(referenceTask, decoded);
    }

    @Test
    void decryptROT13Task() {
        Task encoded = new Task(
                "uMrwjQyk",
                "Tnzoyr",
                1,
                20,
                "Vairfgvtngr Unlvz Fhzzresvryq naq svaq bhg gurve eryngvba gb gur zntvp ubefr.",
                ROT13
        );
        Task decoded = Decoder.decryptIfPossible(encoded);
        assertEquals(referenceTask, decoded);
    }

}
