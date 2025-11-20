package com.soonsoft.uranus.services.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.soonsoft.uranus.services.dictionary.exceptions.DictionaryNotExistsException;
import com.soonsoft.uranus.services.dictionary.model.DictionaryGroup;
import com.soonsoft.uranus.services.dictionary.model.DictionaryItem;
import com.soonsoft.uranus.services.dictionary.service.DictionaryService;

/**
 * Tests for `DictionaryManager` placed in central `uranus.test` module.
 */
public class DictionaryManagerTest {

    @Test
    public void checkDictionaryItemExists_throws_when_missing() {
        DictionaryService service = new DictionaryService(null, null) {
            @Override
            public List<DictionaryGroup> getAllEnabledGroups() {
                return Collections.emptyList();
            }

            @Override
            public List<DictionaryItem> getAllEnabledItems() {
                return Collections.emptyList();
            }
        };

        DictionaryManager manager = new DictionaryManager(service, null);

        try {
            manager.checkDictionaryItemExists("g1", "i1");
            fail("Expected DictionaryNotExistsException");
        } catch (DictionaryNotExistsException ex) {
            assertEquals("g1", ex.getGroupCode());
            assertEquals("i1", ex.getItemCode());
        }
    }

    @Test
    public void checkDictionaryItemExists_passes_when_present() {
        DictionaryItem item = new DictionaryItem();
        item.setDictGroupCode("g1");
        item.setDictItemCode("i1");

        DictionaryGroup group = new DictionaryGroup();
        group.setDictGroupCode("g1");

        DictionaryService service = new DictionaryService(null, null) {
            @Override
            public List<DictionaryGroup> getAllEnabledGroups() {
                return Arrays.asList(group);
            }

            @Override
            public List<DictionaryItem> getAllEnabledItems() {
                return Arrays.asList(item);
            }
        };

        DictionaryManager manager = new DictionaryManager(service, null);

        // should not throw
        manager.checkDictionaryItemExists("g1", "i1");
    }
}
