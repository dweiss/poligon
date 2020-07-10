
package com.carrotsearch.hppc.tests;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntScatterMap;
import com.carrotsearch.hppc.ObjectIntWormMap;
import com.carrotsearch.progresso.LongTracker;
import com.carrotsearch.progresso.Progress;
import com.carrotsearch.progresso.TaskStats;
import com.carrotsearch.progresso.views.console.ConsoleAware;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class WikipediaWordCount {
   public static void main(String[] args) throws Exception {
      //ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
      ObjectIntScatterMap<String> map = new ObjectIntScatterMap<>();
      //ObjectIntWormMap<String> map = new ObjectIntWormMap<>();

      try (Progress progress = new Progress(ConsoleAware.newConsoleProgressView());
           LongTracker tracker = progress.newLongSubtask("lines").start(0)) {
         Pattern p = Pattern.compile("\\s+");
         Path input = Paths.get("d:\\data\\mikemccandless\\enwiki.random.lines.txt");
         long total = 0;
         try (BufferedReader reader = Files.newBufferedReader(input,
             StandardCharsets.ISO_8859_1)) {
            String line;
            while ((line = reader.readLine()) != null) {
               tracker.increment();
               if (tracker.at() > 500_000) {
                  break;
               }
               map.release();
               for (String term : p.split(line)) {
                  total += term.length();
                  if (term.length() < 40) {
                     if (!map.containsKey(term)) {
                        map.put(term, 1);
                     } else {
                        map.put(term, map.get(term) + 1);
                     }
                  }
               }
            }
         }

         tracker.attribute("Total length", "%,d", total);
         tracker.attribute("Terms", "%,d", map.size());
         System.out.println(TaskStats.breakdown(tracker.task()));
      }
   }
}
