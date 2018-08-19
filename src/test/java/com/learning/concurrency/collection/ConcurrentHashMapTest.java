package com.learning.concurrency.collection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ConcurrentHashMapTest {

    /**
     * Better to use concurrentMap.replace() instead of
     * <pre>
     * Long oldValue = concurrentMap.get(word);
     * Long newValue = oldValue == null ? 1L : oldValue +1;
     * concurrentMap.put(word, newValue);
     * </pre>
     * because the code above is not atomicity.
     */
    @Test
    public void testCountWordUsageFrequency() throws InterruptedException {
        ConcurrentMap<String, Long> wordsMap = new ConcurrentHashMap<>();
        String selectedWord = "some_word";
        wordsMap.put(selectedWord, 0L);

        for (int i = 0; i < 10; ++i) {
            new Thread(() -> replaceValueSafely(wordsMap, selectedWord)).start();
        }

        TimeUnit.SECONDS.sleep(2);

        Assertions.assertThat(wordsMap.get(selectedWord)).isEqualTo(10L);
    }

    private void replaceValueSafely(ConcurrentMap<String, Long> wordsMap, String selectedWord) {
        Long oldValue;
        Long newValue;

        do {
            oldValue = wordsMap.get(selectedWord);
            newValue = oldValue == null ? 1L : oldValue + 1;
        } while (!wordsMap.replace(selectedWord, oldValue, newValue));
    }
}
