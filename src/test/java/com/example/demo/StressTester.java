package com.example.demo;

import java.io.PrintStream;
import java.util.ArrayList;
import static java.util.Collections.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StressTester<V> {

  private final Class<V> daCLazz;
  private final int cycles;
  private final List<Exception> exceptions;
  private final List<V> results;
  private final ExecutorService executor;
  private Callable<V>[] callables;

  @SafeVarargs
  public StressTester(Class<V> daClazz, int cycles, int poolSize, Callable<V>... callables) {
    this.daCLazz = daClazz;
    this.callables = callables;
    this.cycles = cycles;
    if (poolSize <= 0) {
      poolSize = Runtime.getRuntime().availableProcessors();
    }
    executor = Executors.newFixedThreadPool(poolSize);
    exceptions = synchronizedList(new ArrayList<>());
    results = synchronizedList(new ArrayList<>());
  }

  public void test() throws InterruptedException, ExecutionException {
    List<Future<V>> futures = new ArrayList<>();
    ExecutorCompletionService<V> completer = new ExecutorCompletionService<>(executor);
    for (int i = 0; i < cycles; i++) {
      for (Callable<V> call : callables) {
        futures.add(completer.submit(call));
      }
    }
    futures.forEach(fut -> {
      try {
        V result = completer.take().get();
        results.add(result);
      } catch (InterruptedException | ExecutionException ex) {
        exceptions.add(ex);
      }
    });
  }

  public List<Exception> getExceptions() {
    return unmodifiableList(exceptions);
  }

  public List<V> getResuls() {
    return unmodifiableList(results);
  }

  public void printErrors(PrintStream ps) {
    if (exceptions.isEmpty()) {
      ps.println("no exceptions");
      return;
    }
    exceptions.forEach(exception -> ps.println(exception.getMessage()));
  }

  public void printResults(PrintStream ps) {
    if (results.isEmpty() || Void.class == daCLazz) {
      ps.println("no results");
      return;
    }
    results.forEach(result -> ps.println(result));
  }
}
